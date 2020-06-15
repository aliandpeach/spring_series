package templatemethod;

public abstract class TemplateMethod
{
    public final void start()
    {
        run();
    }

    protected abstract void run();
}
