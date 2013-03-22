package com.project.mantle_v1.notification_home;

import java.util.List;

import com.project.mantle_v1.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class NoteAdapter extends ArrayAdapter<Note>{

	 public NoteAdapter(Context context, int textViewResourceId,
			List<Note> objects) {
		super(context, textViewResourceId, objects);
	}

	@Override
	    public View getView(int position, View convertView, ViewGroup parent) {
	        return getViewOptimize(position, convertView, parent);
	    }

	    public View getViewOptimize(int position, View convertView, ViewGroup parent) {
	        ViewHolder viewHolder = null;
	        if (convertView == null) {
	            LayoutInflater inflater = (LayoutInflater) getContext()
	                      .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            convertView = inflater.inflate(R.layout.note_layout, null);
	            viewHolder = new ViewHolder();
	            viewHolder.username = (TextView)convertView.findViewById(R.id.textViewUser);
	            viewHolder.note = (TextView)convertView.findViewById(R.id.textViewNote);
	            convertView.setTag(viewHolder);
	        } else {
	            viewHolder = (ViewHolder) convertView.getTag();
	        }
	        Note note = getItem(position);
	        viewHolder.username.setText(note.user + ":");
	        viewHolder.note.setText(note.content);
	        return convertView;
	    }

	    private class ViewHolder {
	        public TextView username;
	        public TextView note;
	    }

}
