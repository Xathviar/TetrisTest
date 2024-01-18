package helper;

import java.io.InputStream;

/**
 * Helper class for loading resources from the classpath.
 */
public class ClasspathResourceLoader {

    /**
     * Represents the name of a resource file to be loaded from the classpath.
     *
     * This variable is used in the {@link ClasspathResourceLoader} class to specify the resource file to load.
     *
     * Usage:
     * String file = "example.txt";
     * ClasspathResourceLoader loader = ClasspathResourceLoader.of(file);
     * InputStream inputStream = loader.getInputStream();
     */
    String file;

    /**
     * Represents a ClassLoader used for loading resources from the classpath.
     */
    ClassLoader classLoader;

    /**
     * Factory method for creating a new ClasspathResourceLoader.
     *
     * @param _file the name of the resource file to load.
     * @return ClasspathResourceLoader instance for the given resource file.
     */
    public static ClasspathResourceLoader of(String _file)
    {
        return of(_file, ClasspathResourceLoader.class.getClassLoader());
    }

    /**
     * Factory method for creating a new ClasspathResourceLoader with a specific ClassLoader.
     *
     * @param _file the name of the resource file to load.
     * @param _cl the ClassLoader to use for loading the resource file; if null, the system ClassLoader will be used.
     * @return ClasspathResourceLoader instance for the given resource file and ClassLoader.
     */
    public static ClasspathResourceLoader of(String _file, ClassLoader _cl)
    {
        if(_cl == null)
        {
            _cl = ClassLoader.getSystemClassLoader();
        }

        ClasspathResourceLoader _rl = new ClasspathResourceLoader();
        _rl.file = _file;
        _rl.classLoader = _cl;
        return _rl;
    }

    /**
     * Returns an InputStream for reading the contents of the resource file.
     *
     * @return an InputStream for the resource file.
     */
    public InputStream getInputStream()
    {
        return this.classLoader.getResourceAsStream(this.file);
    }


}
