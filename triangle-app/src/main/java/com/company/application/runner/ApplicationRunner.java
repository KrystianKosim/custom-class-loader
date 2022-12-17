package com.company.application.runner;

import com.company.application.TriangleController;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

public class ApplicationRunner {

    public static final String RUN = "run";

    /**
     * Method to start application, create an instance of founded class with Controller annotation and run a method defined as RUN
     *
     * @param applicationClass
     */
    public static void run(Class<?> applicationClass) {

        ClassLoader classLoader;
        try {
            classLoader = ApplicationClassLoader.getInstance(applicationClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (classLoader instanceof ApplicationClassLoader) {
            ApplicationClassLoader applicationClassLoader = (ApplicationClassLoader) classLoader;
            try {
                Class<?> controller = applicationClassLoader.findAnnotatedClass(Controller.class);
                Object someController = controller.newInstance();
                controller.getMethod(RUN).invoke(someController);
            } catch (ClassNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            TriangleController triangleController = new TriangleController();
            triangleController.run();
        }
    }

    private static class ApplicationClassLoader extends ClassLoader {


        public static final String JAR = ".jar";
        public static final String CLASS = ".class";
        private static Map<String, byte[]> classMap = new ConcurrentHashMap<>();

        /**
         * Method to load Application, if jarName ends with .jar,this method load classes entries in jar and looking for subJars
         *
         * @param applicationClass, the class to run
         * @throws IOException
         */
        public void loadApplication(Class<?> applicationClass) throws IOException {
            String jarName = new File(applicationClass.getProtectionDomain().getCodeSource().getLocation().getPath()).getAbsolutePath();
            if (jarName.endsWith(JAR)) {
                JarFile jarFile = new JarFile(jarName);
                Map<JarEntry, byte[]> entries = findJarEntries(jarFile);
                loadJarEntries(entries);
            }
        }

        public static ClassLoader getInstance(Class<?> applicationClass) throws IOException {
            String jarName = new File(applicationClass
                    .getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .getPath()).getAbsolutePath();
            if (jarName.endsWith(JAR)) {
                ApplicationClassLoader classLoader = new ApplicationClassLoader();
                classLoader.loadApplication(applicationClass);
                return classLoader;
            }
            return applicationClass.getClassLoader();
        }


        /**
         * This method should find class with annotation given like param
         *
         * @param annotation
         * @return
         * @throws ClassNotFoundException
         */
        public Class<?> findAnnotatedClass(Class annotation) throws ClassNotFoundException {
            Optional<String> op = getOptionalOfClassAnnotation(annotation);
            if (op.isPresent()) {
                return loadClass(op.get());
            }
            return null;
        }

        /**
         * This method load every items from map given as parametr to classMap and to ClassLoader
         *
         * @param entries
         */
        private void loadJarEntries(Map<JarEntry, byte[]> entries) {
            entries.forEach((jarEntry, bytes) -> {
                try {
                    loadClassToClassLoader(jarEntry, bytes);
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            });
        }

        /**
         * This method turns the name of jarEntry given as parametr and add class to class loader and to classMap
         *
         * @param jarEntry
         * @param bytes
         * @throws IOException
         */
        private void loadClassToClassLoader(JarEntry jarEntry, byte[] bytes) throws IOException {
            String className = editStringFormatOfJarEntry(jarEntry);
            addClass(className, bytes);
            addClassToClassLoader(className);
        }

        /**
         * This method should find files with extension like: ".jar" and ".class"
         *
         * @param jarFile
         * @return Map on which the classes are built
         */
        private Map<JarEntry, byte[]> findJarEntries(JarFile jarFile) {
            Map<JarEntry, byte[]> jarEntries = new HashMap<>();
            jarFile.stream().forEach(jarEntry -> {
                if (jarEntry.getName().endsWith(JAR)) {
                    jarEntries.putAll(findSubJarEntries(jarFile, jarEntry));
                } else if (jarEntry.getName().endsWith(CLASS)) {
                    try {
                        InputStream jarInputStream = jarFile.getInputStream(jarEntry);
                        jarEntries.put(jarEntry, readJarInputStream(jarInputStream));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            return jarEntries;
        }


        /**
         * This method should find files with extension like ".class" in subJar
         *
         * @param jarFile,  main jar file, in which is another jar file
         * @param jarEntry, in which method will try to find a files with extension ".class"
         * @return Map on which the classes are built
         */
        private Map<? extends JarEntry, ? extends byte[]> findSubJarEntries(JarFile jarFile, JarEntry jarEntry) {
            Map<JarEntry, byte[]> jarEntries = new HashMap<>();
            try {
                JarInputStream jarIS = new JarInputStream(jarFile.getInputStream(jarEntry));
                JarEntry inEntry = jarIS.getNextJarEntry();
                while (inEntry != null) {
                    if (inEntry.getName().endsWith(CLASS)) {
                        jarEntries.put(inEntry, readJarInputStream(jarIS));
                    }
                    inEntry = jarIS.getNextJarEntry();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return jarEntries;
        }

        /**
         * This method return a byte[] tab, which is necessary to class loader
         *
         * @param jarIS, input stream from which method should read bytes
         * @return byte []
         * @throws IOException
         */
        private byte[] readJarInputStream(InputStream jarIS) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int bytesNumRead = 0;
            while ((bytesNumRead = jarIS.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesNumRead);
            }
            return baos.toByteArray();
        }


        /**
         * This method should edit String to correct format for loadClassToClassLoader method
         *
         * @param innerEntry, the name of this innerEntry should be edited
         * @return Correct format of String for loadClassToClassLoader method
         */
        private String editStringFormatOfJarEntry(JarEntry innerEntry) {
            return innerEntry.getName().replace("\\", ".").replace("/", ".").replace(".class", "");
        }

        /**
         * This method should addClass to classMap
         *
         * @param className
         * @param byteCode
         * @return
         */
        private boolean addClass(String className, byte[] byteCode) {
            if (!classMap.containsKey(className)) {
                classMap.put(className, byteCode);
                return true;
            }
            return false;
        }

        /**
         * This method should add class which is in classMap to classLoader
         *
         * @param name, name of class which should be added
         * @return
         */
        private Class<?> addClassToClassLoader(String name) {
            byte[] result = getClass(name);
            try {
                if (result == null) {
                    throw new ClassNotFoundException();
                } else {
                    return defineClass(name, result, 0, result.length);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }


        /**
         * This method should return class from classMap, if the class with name given in parametr exist
         *
         * @param className
         * @return
         */
        private byte[] getClass(String className) {
            if (classMap.containsKey(className)) {
                return classMap.get(className);
            } else {
                return null;
            }
        }


        private Optional<String> getOptionalOfClassAnnotation(Class annotation) {
            Optional<String> op = classMap.keySet().stream().filter(className -> {
                Class<?> annotatedClass;
                try {
                    annotatedClass = loadClass(className);
                } catch (ClassNotFoundException e) {
                    return false;
                }
                return checkAnnotation(annotation, annotatedClass);
            }).findAny();
            return op;
        }

        private boolean checkAnnotation(Class annotation, Class<?> annotatedClass) {
            for (Annotation a : annotatedClass.getAnnotations()) {
                if (a.annotationType().getName().equals(annotation.getName())) {
                    return true;
                }
            }
            return false;
        }

    }
}
