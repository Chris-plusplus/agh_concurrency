const { performance } = require('perf_hooks');
const fs = require('fs').promises;


let asyncForkFilepath = "asyncForkFilepath.txt"
let conductorForkFilepath = "conductorForkFilepath.txt"
let conductorWaiterFilepath = "conductorWaiterFilepath.txt"

let fileLock = {
    asyncForkFilepath: false,
    conductorForkFilepath: false,
    conductorWaiterFilepath: false
};

setTimeoutTimeout = 0;

fs.writeFile(asyncForkFilepath, "");
fs.writeFile(conductorForkFilepath, "");
fs.writeFile(conductorWaiterFilepath, "");

async function appendFileSafely(filename, data) {
    while (fileLock[filename]) {
        await new Promise(resolve => setTimeout(resolve, setTimeoutTimeout));
    }

    fileLock[filename] = true;

    try {
        await fs.appendFile(filename, data);
    } finally {
        fileLock[filename] = false;
    }
}

var Fork = function() {
    this.state = 0;
    return this;
}

Fork.prototype.acquire = function (filename, phil, cb) {
    const start = performance.now();
    const tryAcquire = () => {
        if (this.state === 0) {
            const diff = performance.now() - start;
            appendFileSafely(filename, `${phil.id}\t${diff}\n`);
            this.state = 1;
            phil.forkTimeout = 1;
            cb();
        } else {
            setTimeout(() => {
                phil.forkTimeout *= 2; // Exponential backoff
                tryAcquire();
            }, phil.forkTimeout);
        }
    };

    tryAcquire();
};

Fork.prototype.release = function() { 
    this.state = 0; 
}

var Philosopher = function(id, forks, waiter = null) {
    this.id = id;
    this.forks = forks;
    this.f1 = id % forks.length;
    this.forkTimeout = 1;
    this.f2 = (id+1) % forks.length;
    this.waiter = waiter;
    if(this.waiter != null){
        this.waiterTimeout = 1;
    }
    return this;
}

var Waiter = function(N) {
    this.state = N;
    return this;
}

Waiter.prototype.acquire = function(phil, cb){
    const start = performance.now();
    var fn = () => {
        // wolne miejsce
        if(this.state > 0){
            const diff = performance.now() - start;
            appendFileSafely(conductorWaiterFilepath, `${phil.id}\t${diff}\n`);
            --this.state;
            phil.waiterTimeout = 1;
            cb();
        }
        // brak wolnego miejsca, zakolejkuj
        else{
            setTimeout(() => {
                phil.waiterTimeout *= 2;
                fn();
            }, phil.waiterTimeout);
        }
    }

    fn();
}

Waiter.prototype.release = function(phil){
    ++this.state;
    // jeśli istnieje, odpal pierwszą funkcję w kolejce
    if(this.queue.length > 0) {
        this.queue.shift()();
    }
}

Philosopher.prototype.startNaive = function(count) {
    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
        id = this.id;
    
    // zaimplementuj rozwiazanie naiwne
    // kazdy filozof powinien 'count' razy wykonywac cykl
    // podnoszenia widelcow -- jedzenia -- zwalniania widelcow

    cycle = function (i) {
        if(i < count){
            forks[f1].acquire(this, () => {
                console.log(`${id}: ${f1}^`);
                forks[f2].acquire(this, () => {
                    console.log(`${id}: ${f2}^`);
                    setTimeout(() => {
                        forks[f2].release()
                        console.log(`${id}: ${f2}v`);
                        forks[f1].release()
                        console.log(`${id}: ${f1}v`);
                        setTimeout(cycle, 0, i + 1)
                    }, setTimeoutTimeout)
                })
            })
        }
    }
    cycle(0)
}

Philosopher.prototype.startAsym = function(count) {
    var forks = this.forks,
        id = this.id;

    var f1 = id % 2 === 0 ? this.f2 : this.f1;
    var f2 = id % 2 === 0 ? this.f1 : this.f2;

    var cycle = (i) => {
        if (i >= count){
            return;
        }

        //console.log(`${id} cycle: ${i}`);
        forks[f1].acquire(asyncForkFilepath, this, () => {
            //console.log(`${id}: ${f1}^`);
            forks[f2].acquire(asyncForkFilepath, this, () => {
                //console.log(`${id}: ${f2}^`);
                //console.log(`${id} cycle: ${i}, start`);
                setTimeout(() => {
                    forks[f2].release();
                    //console.log(`${id}: ${f2}v`);
                    forks[f1].release();
                    //console.log(`${id}: ${f1}v`);
                    //console.log(`${id} cycle: ${i}, end`);
                    setTimeout(cycle, 0, i + 1)
                    // cycle(i + 1);
                }, /* Math.random() * */ setTimeoutTimeout);
            });
        });
    };
    cycle(0);
};

Philosopher.prototype.startConductor = function(count) {
    var forks = this.forks,
        f1 = this.f1,
        f2 = this.f2,
        id = this.id,
        waiter = this.waiter;
    
    // zaimplementuj rozwiazanie z kelnerem
    // kazdy filozof powinien 'count' razy wykonywac cykl
    // podnoszenia widelcow -- jedzenia -- zwalniania widelcow

    var cycle = (i) => {
        if(i >= count){
            return;
        }

        //console.log(`${id} cycle: ${i}`);
        waiter.acquire(this, () => {
            //console.log(`${id} is being handled`)
            forks[f1].acquire(conductorForkFilepath, this, () => {
                //console.log(`${id}: ${f1}^`)
                forks[f2].acquire(conductorForkFilepath, this, () => {
                    //console.log(`${id}: ${f2}^`)
                    //console.log(`${id} cycle: ${i}, start`);
                    setTimeout(() => {
                        forks[f2].release();
                        //console.log(`${id}: ${f2}v`);
                        forks[f1].release();
                        //console.log(`${id}: ${f1}v`);
                        //console.log(`${id} cycle: ${i}, end`);
                        waiter.release();
                        //console.log(`${id} was handled`);
                        setTimeout(cycle, 0, i + 1)
                        // cycle(i + 1);
                    }, /* Math.random() * */ setTimeoutTimeout);
                });
            });
        });
    }
    cycle(0);
}


var N = 100;
var forks = [];
var philosophers = []
for (var i = 0; i < N; i++) {
    forks.push(new Fork());
}

var waiter = new Waiter(N - 1)

for (var i = 0; i < N; i++) {
    philosophers.push(new Philosopher(i, forks, waiter));
}

for (var i = 0; i < N; i++) {
    // philosophers[i].startAsym(100);
    philosophers[i].startConductor(100);
}