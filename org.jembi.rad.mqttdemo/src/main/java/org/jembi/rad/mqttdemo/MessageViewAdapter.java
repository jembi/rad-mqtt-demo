package org.jembi.rad.mqttdemo;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jembi.rad.mqttdemo.model.Message;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Message}
 */
public class MessageViewAdapter extends RecyclerView.Adapter<MessageViewAdapter.ViewHolder> {

    private final List<Message> values;

    public MessageViewAdapter(List<Message> items) {
        if (items == null) {
            // ensure the list is always initialised
            items = new ArrayList<>();
        }
        values = items;
    }

    public void addMessage(Message message) {
        values.add(0, message);
        notifyItemInserted(0);
        Log.i("LOG", "Incoming message: " + message.getMessage());
        System.out.println("Message index" + values.get(0).getMessage());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.i("LOG", "onBind: " + position);
        holder.item = values.get(position);
        holder.dateTimeView.setText(DateFormat.getDateTimeInstance().format(holder.item.getDatetime()));
        holder.messageView.setText(holder.item.getMessage());
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView dateTimeView;
        public final TextView messageView;
        public Message item;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            dateTimeView = (TextView) view.findViewById(R.id.datetime);
            messageView = (TextView) view.findViewById(R.id.message);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + messageView.getText() + "'";
        }
    }
}
