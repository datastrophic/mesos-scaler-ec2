package conf;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

/**
 * Created by ssa on 2015-10-09 14:32
 */
public class NinjaTest {

    @Test
    public void testExtractPublicIp(){
        String raw = "52.19.198.178ubuntu@ip-172-31-7-129";
        String publicIp = Ninja.parsePublicIp(raw);
        assertThat(publicIp, equalTo("52.19.198.178"));
    }

}