package se.dennisvonbargen.openlogger.dialogs;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import se.dennisvonbargen.openlogger.R;

/**
 *
 * Created by dennis on 2016-07-25.
 */
public final class Alert {

    public static AlertDialog getAlert(Activity activity, String title, String message,
                                       DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(message)
                .setTitle(title)
                .setNeutralButton(R.string.com_ok, listener);
        return builder.create();
    }

    public static AlertDialog getAlert(Activity activity, int titleId, int messageId,
                                                    DialogInterface.OnClickListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(activity.getString(messageId))
                .setTitle(activity.getString(titleId))
                .setNeutralButton(R.string.com_ok, listener);
        return builder.create();
    }
}
