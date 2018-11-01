package in.nvm_abhinav_vutukuri.apps.android.wikipediasearch;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import java.io.IOException;

final class Utils
{
    static boolean hasInternetAccess() throws InterruptedException, IOException
    {
        final String command = "ping -c 1 google.com";
        return Runtime.getRuntime()
                      .exec(command)
                      .waitFor() == 0;
    }

    static void showAlertDialog(Context context, int titleResID, int msgResID)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titleResID);
        builder.setMessage(msgResID);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
            }
        });
        builder.show();
    }

    static void showDailogOnUIthread(final Activity activity, final int titleResID, final int msgResID)
    {
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Utils.showAlertDialog(activity, titleResID, msgResID);
            }
        });
    }

    static void showAlertDialog(Context context, int titleResID, String msg)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(titleResID);
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialogInterface, int i)
            {
            }
        });
        builder.show();
    }

    static void showDailogOnUIthread(final Activity activity, final int titleResID, final String msg)
    {
        activity.runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                Utils.showAlertDialog(activity, titleResID, msg);
            }
        });
    }
}
