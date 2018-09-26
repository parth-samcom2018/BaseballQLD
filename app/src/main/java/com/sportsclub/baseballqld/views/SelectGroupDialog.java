package com.sportsclub.baseballqld.views;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.widget.NumberPicker;

import com.sportsclub.baseballqld.models.Group;

import java.util.List;

public class SelectGroupDialog extends AlertDialog {

    public interface Protocol
    {
        public void didSelectGroup(Group group);
    }

    public SelectGroupDialog(Context context, final List<Group> groups, final Protocol delegatte) {
        super(context);

        this.setTitle("Select Group");

        //View v = ((Activity)context).getLayoutInflater().inflate(R.layout.uploadphoto_dialog,null);

        final NumberPicker picker = new NumberPicker(context);
        picker.setMinValue(0);
        picker.setMaxValue(groups.size() - 1);
        picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS); //stops editing...
        String[] sa = new String[groups.size()];
        for(int i=0; i<groups.size(); i++)
        {
            sa[i] = groups.get(i).groupName;
            // Log.d("hq","album id:"+albums.get(i).mediaAlbumId);
        }
        picker.setDisplayedValues(sa);

        this.setView(picker);

        this.setButton(BUTTON_POSITIVE, "Select", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int index = picker.getValue();
                Group g = groups.get(index);
                delegatte.didSelectGroup(g);
            }
        });

        this.setButton(BUTTON_NEGATIVE, "Cancel", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

    }



}
