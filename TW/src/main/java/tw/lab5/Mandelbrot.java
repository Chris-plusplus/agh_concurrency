package tw.lab5;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.*;
import javax.swing.JFrame;

public class Mandelbrot extends JFrame {

    private final int MAX_ITER = 570;
    private final double ZOOM = 150;
    private BufferedImage I;
    private static final int AVAILABLE_THREADS = Runtime.getRuntime().availableProcessors();

    private static final int TASK_TYPE = 0;
    private static final int TIMES = 20;

    private static final int WIDTH = 1920;
    private static final int HALF_WIDTH = 1920 / 2;
    private static final int HEIGHT = 1080;
    private static final int HALF_HEIGHT = 1080 / 2;

    ExecutorService executor1 = Executors.newFixedThreadPool(1);
    ExecutorService executorN = Executors.newFixedThreadPool(AVAILABLE_THREADS);
    ExecutorService executor4N = Executors.newFixedThreadPool(4 * AVAILABLE_THREADS);

    void resetExecutors(){
        executor1 = Executors.newFixedThreadPool(1);
        executorN = Executors.newFixedThreadPool(AVAILABLE_THREADS);
        executor4N = Executors.newFixedThreadPool(4 * AVAILABLE_THREADS);
    }

    private void runTaskPerPixel(ExecutorService executor){
        var futures = new ArrayList<Future<?>>();

        for (int y = 0; y < getHeight(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                final int finalX = x;
                final int finalY = y;

                futures.add(executor.submit(() -> {
                    double zx = 0, zy = 0, cX = (finalX - HALF_WIDTH) / ZOOM, cY = (finalY - HALF_HEIGHT) / ZOOM, tmp;
                    int iter = MAX_ITER;
                    while (zx * zx + zy * zy < 4 && iter > 0) {
                        tmp = zx * zx - zy * zy + cX;
                        zy = 2.0 * zx * zy + cY;
                        zx = tmp;
                        iter--;
                    }

                    I.setRGB(finalX, finalY, iter | (iter << 8));
                }));
            }
        }

        for(var future : futures){
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void runTask(int taskCount, ExecutorService executor){
        var futures = new ArrayList<Future<?>>();
        final int taskSize = (WIDTH * HEIGHT + taskCount - 1) / taskCount;

        for(int i = 0; i * taskSize < WIDTH * HEIGHT; ++i){
            final int taskBegin = i * taskSize;
            final int taskEnd = Math.min((i + 1) * taskSize, WIDTH * HEIGHT);;

            //System.out.println("[(%d, %d); (%d, %d))".formatted(_x, _y, _endX, _endY));

            futures.add(executor.submit(() -> {
                int x = taskBegin % WIDTH;
                int y = taskBegin / WIDTH;
                int endX = taskEnd % WIDTH;
                int endY = taskEnd / WIDTH;
                for(; y < getHeight(); ++y){
                    for(; x < getWidth(); ++x){
                        if(x == endX && y == endY){
                            return;
                        }
                        double zx = 0, zy = 0, cX = (x - HALF_WIDTH) / ZOOM, cY = (y - HALF_HEIGHT) / ZOOM, tmp;
                        int iter = MAX_ITER;
                        while (zx * zx + zy * zy < 4 && iter > 0) {
                            tmp = zx * zx - zy * zy + cX;
                            zy = 2.0 * zx * zy + cY;
                            zx = tmp;
                            iter--;
                        }

                        I.setRGB(x, y, iter | (iter << 8));
                    }
                    x = 0;
                }
            }));
        }

        for(var future : futures){
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Mandelbrot() throws IOException {
        super("Mandelbrot Set");
        setBounds(0, 0, WIDTH, HEIGHT);
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        I = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);

        FileWriter _1t_1px_txt = new FileWriter("_1t_1px.txt");
        FileWriter _Nt_1px_txt = new FileWriter("_Nt_1px.txt");
        FileWriter _4Nt_1px_txt = new FileWriter("_4Nt_1px.txt");

        FileWriter _1t_t_txt = new FileWriter("_1t_t.txt");
        FileWriter _Nt_t_txt = new FileWriter("_Nt_t.txt");
        FileWriter _4Nt_t_txt = new FileWriter("_4Nt_t.txt");

        FileWriter _1t_10t_txt = new FileWriter("_1t_10t.txt");
        FileWriter _Nt_10t_txt = new FileWriter("_Nt_10t.txt");
        FileWriter _4Nt_10t_txt = new FileWriter("_4Nt_10t.txt");

        for(int i = 0; i != TIMES; ++i){
            var start = System.nanoTime();
            runTaskPerPixel(executor1);
            var end = System.nanoTime();
            _1t_1px_txt.write("%d\t%d\n".formatted(i, end - start));
        }
        System.out.println("_1t_1px_txt done");
        for(int i = 0; i != TIMES; ++i){
            var start = System.nanoTime();
            runTaskPerPixel(executorN);
            var end = System.nanoTime();
            _Nt_1px_txt.write("%d\t%d\n".formatted(i, end - start));
        }
        System.out.println("_Nt_1px_txt done");
        for(int i = 0; i != TIMES; ++i){
            var start = System.nanoTime();
            runTaskPerPixel(executor4N);
            var end = System.nanoTime();
            _4Nt_1px_txt.write("%d\t%d\n".formatted(i, end - start));
        }
        System.out.println("_4Nt_1px_txt done");
        resetExecutors();

        for(int i = 0; i != TIMES; ++i){
            var start = System.nanoTime();
            runTask(AVAILABLE_THREADS, executor1);
            var end = System.nanoTime();
            _1t_t_txt.write("%d\t%d\n".formatted(i, end - start));
        }
        System.out.println("_1t_t_txt done");
        for(int i = 0; i != TIMES; ++i){
            var start = System.nanoTime();
            runTask(AVAILABLE_THREADS, executorN);
            var end = System.nanoTime();
            _Nt_t_txt.write("%d\t%d\n".formatted(i, end - start));
        }
        System.out.println("_Nt_t_txt done");
        for(int i = 0; i != TIMES; ++i){
            var start = System.nanoTime();
            runTask(AVAILABLE_THREADS, executor4N);
            var end = System.nanoTime();
            _4Nt_t_txt.write("%d\t%d\n".formatted(i, end - start));
        }
        System.out.println("_4Nt_t_txt done");
        resetExecutors();

        for(int i = 0; i != TIMES; ++i){
            var start = System.nanoTime();
            runTask(10 * AVAILABLE_THREADS, executor1);
            var end = System.nanoTime();
            _1t_10t_txt.write("%d\t%d\n".formatted(i, end - start));
        }
        System.out.println("_1t_10t_txt done");
        for(int i = 0; i != TIMES; ++i){
            var start = System.nanoTime();
            runTask(10 * AVAILABLE_THREADS, executorN);
            var end = System.nanoTime();
            _Nt_10t_txt.write("%d\t%d\n".formatted(i, end - start));
        }
        System.out.println("_Nt_10t_txt done");
        for(int i = 0; i != TIMES; ++i){
            var start = System.nanoTime();
            runTask(10 * AVAILABLE_THREADS, executor4N);
            var end = System.nanoTime();
            _4Nt_10t_txt.write("%d\t%d\n".formatted(i, end - start));
        }
        System.out.println("_4Nt_10t_txt done");
        resetExecutors();

        _1t_1px_txt.close();
        _Nt_1px_txt.close();
        _4Nt_1px_txt.close();
        _1t_t_txt.close();
        _Nt_t_txt.close();
        _4Nt_t_txt.close();
        _1t_10t_txt.close();
        _Nt_10t_txt.close();
        _4Nt_10t_txt.close();

//        var futures = new ArrayList<Future<?>>();
//
//        if(TASK_TYPE == 0){ // TASK_COUNT == THREADS
//            final int taskSize = (WIDTH * HEIGHT + THREADS - 1) / THREADS;
//            System.out.println(taskSize);
//            for(int i = 0; i * taskSize < WIDTH*HEIGHT; ++i){
//                final int taskBegin = i * taskSize;
//                final int taskEnd = Math.min((i + 1) * taskSize, WIDTH * HEIGHT);;
//
//                int _x = taskBegin % WIDTH;
//                int _y = taskBegin / WIDTH;
//                int _endX = taskEnd % WIDTH;
//                int _endY = taskEnd / WIDTH;
//
//                //System.out.println("[(%d, %d); (%d, %d))".formatted(_x, _y, _endX, _endY));
//
//                futures.add(executor.submit(() -> {
//                    int x = taskBegin % WIDTH;
//                    int y = taskBegin / WIDTH;
//                    int endX = taskEnd % WIDTH;
//                    int endY = taskEnd / WIDTH;
//                    for(; y < getHeight(); ++y){
//                        for(; x < getWidth(); ++x){
//                            if(x == endX && y == endY){
//                                return;
//                            }
//                            double zx = 0, zy = 0, cX = (x - HALF_WIDTH) / ZOOM, cY = (y - HALF_HEIGHT) / ZOOM, tmp;
//                            int iter = MAX_ITER;
//                            while (zx * zx + zy * zy < 4 && iter > 0) {
//                                tmp = zx * zx - zy * zy + cX;
//                                zy = 2.0 * zx * zy + cY;
//                                zx = tmp;
//                                iter--;
//                            }
//
//                            //if(x == 0)
//                                //System.out.println("%d %d".formatted(x, y));
//
//                            I.setRGB(x, y, iter | (iter << 8));
//                        }
//                        x = 0;
//                    }
//                }));
//            }
//        } else if(TASK_TYPE == 1){ // TASK_COUNT == 10 * THREADS
//            final int taskSize = (WIDTH*HEIGHT + (THREADS * 10) - 1) / (THREADS *10);
//            System.out.println(taskSize);
//            for(int i = 0; i * taskSize < WIDTH*HEIGHT; ++i){
//                final int taskBegin = i * taskSize;
//                final int taskEnd = Math.min((i + 1) * taskSize, WIDTH * HEIGHT);;
//
//                int _x = taskBegin % WIDTH;
//                int _y = taskBegin / WIDTH;
//                int _endX = taskEnd % WIDTH;
//                int _endY = taskEnd / WIDTH;
//
//                //System.out.println("[(%d, %d); (%d, %d))".formatted(_x, _y, _endX, _endY));
//
//                futures.add(executor.submit(() -> {
//                    int x = taskBegin % WIDTH;
//                    int y = taskBegin / WIDTH;
//                    int endX = taskEnd % WIDTH;
//                    int endY = taskEnd / WIDTH;
//                    for(; y < getHeight(); ++y){
//                        for(; x < getWidth(); ++x){
//                            if(x == endX && y == endY){
//                                return;
//                            }
//                            double zx = 0, zy = 0, cX = (x - HALF_WIDTH) / ZOOM, cY = (y - HALF_HEIGHT) / ZOOM, tmp;
//                            int iter = MAX_ITER;
//                            while (zx * zx + zy * zy < 4 && iter > 0) {
//                                tmp = zx * zx - zy * zy + cX;
//                                zy = 2.0 * zx * zy + cY;
//                                zx = tmp;
//                                iter--;
//                            }
//
//                            //if(x == 0)
//                            //System.out.println("%d %d".formatted(x, y));
//
//                            I.setRGB(x, y, iter | (iter << 8));
//                        }
//                        x = 0;
//                    }
//                }));
//            }
//        } else if(TASK_TYPE == 2){ // TASK_SIZE == 1px
//
//        }
//
//        for(var future : futures){
//            try {
//                future.get();
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            } catch (ExecutionException e) {
//                throw new RuntimeException(e);
//            }
//        }
    }

    @Override
    public void paint(Graphics g) {
        g.drawImage(I, 0, 0, this);
    }

    public static void main(String[] args) throws IOException {
        new Mandelbrot().setVisible(true);;
    }
}
