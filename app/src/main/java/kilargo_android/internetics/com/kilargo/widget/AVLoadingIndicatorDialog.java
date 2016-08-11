package kilargo_android.internetics.com.kilargo.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import kilargo_android.internetics.com.kilargo.R;

/**
 * Created by BourneWang on 6/07/2016.
 */
public class AVLoadingIndicatorDialog extends AlertDialog {

    private TextView mMessageView;

    public AVLoadingIndicatorDialog(Context context) {
        super(context);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.progress_indicator_avld, null);
        mMessageView = (TextView) view.findViewById(R.id.message);

        //transparent dialog background does not support on <5.0
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            view.setBackgroundColor(Color.GRAY);
        }

        setView(view,0,0,0,0);//remove border  http://stackoverflow.com/questions/10433764/alertdialog-how-to-remove-black-borders-above-and-below-view
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));  //http://stackoverflow.com/questions/8117849/remove-black-background-on-custom-dialog
    }


    @Override
    public void setMessage(CharSequence message) {
        mMessageView.setText(message);
    }
}
