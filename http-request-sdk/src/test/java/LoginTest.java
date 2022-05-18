import com.yk.auth.LoginAuth;
import com.yk.core.PropertyLoader;
import com.yk.core.SdkExecutors;
import org.junit.Test;

import java.io.IOException;

public class LoginTest
{
    @Test
    public void main() throws IOException
    {
        SdkExecutors.create().init(PropertyLoader.loadProperties());
        LoginAuth.INSTANCE.login();
    }
}
