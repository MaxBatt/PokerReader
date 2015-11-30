package beatmax.pokerreader.debug;

import android.content.Context;
import android.content.Intent;

import com.github.pedrovgs.lynx.LynxActivity;

/**
 * Created by fabdeuch on 14.07.2015.
 *
 * simple utility class that contains debugging methods
 */
public class DebugUtil
{
    public static void startLogCatActivity(Context context)
    {
        Intent lynxActivityIntent = new Intent(context, LynxActivity.class);
        lynxActivityIntent.setFlags(268435456);
        context.startActivity(lynxActivityIntent);
    }
}
