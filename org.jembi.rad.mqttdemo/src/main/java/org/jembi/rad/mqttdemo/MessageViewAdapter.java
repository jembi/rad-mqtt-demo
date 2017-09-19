package org.jembi.rad.mqttdemo;

import android.support.v7.util.SortedList;
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

    private final SortedList<Message> values;

    public MessageViewAdapter(List<Message> items) {
        values = new SortedList<Message>(Message.class, new SortedList.Callback<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                return o2.getDatetime().compareTo(o1.getDatetime());
            }

            @Override
            public void onInserted(int position, int count) {
                notifyItemRangeInserted(position, count);
            }

            @Override
            public void onRemoved(int position, int count) {
                notifyItemRangeRemoved(position, count);
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
                notifyItemMoved(fromPosition, toPosition);
            }

            @Override
            public void onChanged(int position, int count) {
                notifyItemRangeChanged(position, count);
            }

            @Override
            public boolean areContentsTheSame(Message oldItem, Message newItem) {
                return oldItem.equals(newItem);
            }

            @Override
            public boolean areItemsTheSame(Message item1, Message item2) {
                return item1 == item2;
            }
        });
        values.addAll(items);
    }

    public int addMessage(Message message) {
        int index = values.add(message);
        notifyItemInserted(index);
        Log.i(RadMQTTDemoApplication.LOG_TAG, "Incoming message: " + message.getMessage() + " added at position " + index);
        return index;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
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
