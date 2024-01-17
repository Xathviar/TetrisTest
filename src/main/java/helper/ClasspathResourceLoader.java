package helper;

import java.io.InputStream;

public class ClasspathResourceLoader
{
    String file;
    ClassLoader classLoader;

    public static ClasspathResourceLoader of(String _file)
    {
        return of(_file, ClasspathResourceLoader.class.getClassLoader());
    }

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

    public InputStream getInputStream()
    {
        return this.classLoader.getResourceAsStream(this.file);
    }


}
