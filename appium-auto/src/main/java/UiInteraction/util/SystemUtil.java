package UiInteraction.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

public class SystemUtil {

    private static int N_THREADS = 20;
    private static int DEFAULT_TIMEOUT = 1;
    private static final int MAX_PORT = 65535;
    private static String LOCALHOST = "127.0.0.1";
    public static int DEFAULT_STARTING_PORT = 4723;


    /**
     * Returns all top files listed in specified file directory
     *
     * @param dir -  specified file directory
     * @return  top files listed files in dir as List<String>
     */
    public static List<String> getFileListInDirectory(String dir){
        List<String> env = new LinkedList<>();
        File folder = new File(dir);
        File[] files = folder.listFiles();

        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                env.add(files[i].getName());
            }
        }

        return env;
    }

    /**
     * Execute external system command
     *
     * @param outputList - shell output of command to List<String>
     * @param command - system command to invoke
     * @return
     */
    public static int cmd(List<String> outputList, String... command) {
        String result = null;
        try {
            ProcessBuilder pb = new ProcessBuilder(command);
            pb.redirectErrorStream(true);

            Process p = pb.start();

            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = in.readLine();

            while (line != null && line.length() > 0) {
                System.out.println(line);
                if (outputList != null){ outputList.add(line);}
                line = in.readLine();
            }

            in.close();

            Field f = p.getClass().getDeclaredField("pid");
            f.setAccessible(true);
            return (int) f.get(p);
        } catch (IOException e) {
            System.out.println(e);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return -1;
    }


    /**
     * Get a number (numports) of open network ports
     *
     * @param numPorts - number of free ports to retrieve
     * @param startPort - starting port to test from
     * @param range - range of ports to test range >> numports
     * @return
     */
    public static List<Integer> getAvailablePorts(int numPorts, int startPort, int range){
        startPort = (startPort==0) ? DEFAULT_STARTING_PORT : startPort;
        final ExecutorService es = Executors.newFixedThreadPool(N_THREADS);

        final List<Future<Port>> futures = new ArrayList<>();

        try {
            for (int port = startPort; port <= startPort + range; port++) {
                futures.add(portIsOpen(es, LOCALHOST, port, DEFAULT_TIMEOUT));
            }
            es.awaitTermination(200L, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        int openPorts = 0;
        List<Integer> availablePorts = new LinkedList<>();

        numPorts = (numPorts > futures.size()) ? futures.size() : numPorts;
        for (final Future<Port> f : futures) {
            if(openPorts >= numPorts){break;}
            try {
                if (f.get().open()) {
                    openPorts++;
                    availablePorts.add(f.get().getPort());
                }
            } catch (InterruptedException e) {
                continue;
            } catch (ExecutionException e) {
                continue;
            }
        }

        return availablePorts;
    }

    private static Future<Port> portIsOpen(final ExecutorService es, final String ip, final int port, final int timeout) {
        return es.submit(new Callable<Port>() {
            @Override public Port call() {
                ServerSocket ss = null;
                DatagramSocket ds = null;

                try {
                    ss = new ServerSocket(port);
                    ss.setReuseAddress(true);
                    ds = new DatagramSocket(port);
                    ds.setReuseAddress(true);

                    return new Port(port, true);
                } catch (IOException ex) {
                    return new Port(port, false);
                } finally {
                    if (ds != null) {
                        ds.close();
                    }

                    if (ss != null) {
                        try {
                            ss.close();
                        } catch (IOException e) {
                            /* should not be thrown */
                        }
                    }

                }
            }
        });
    }

    private static class Port {
        private int port;
        private boolean isOpen;

        public Port(int port, boolean isOpen) {
            super();
            this.port = port;
            this.isOpen = isOpen;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public boolean open() {
            return isOpen;
        }

        public void setOpen(boolean isOpen) {
            this.isOpen = isOpen;
        }
    }


}
