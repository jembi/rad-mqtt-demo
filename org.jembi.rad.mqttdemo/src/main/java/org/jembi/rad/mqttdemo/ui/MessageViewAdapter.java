package org.jembi.rad.mqttdemo.ui;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.jembi.rad.mqttdemo.R;
import org.jembi.rad.mqttdemo.RadMQTTDemoApplication;
import org.jembi.rad.mqttdemo.model.Message;

import java.text.DateFormat;
import java.util.List;

/**
 * MessageViewAdapter is used to manage the message data displayed in the Message RecyclerView (see SubscribeActivity).
 * The messages are stored in a sorted list that orders messages by date/time received so the latest messages
 * are at the top of the list.
 *
 * Messages can be added to the list when the arrive, using the method addMessage.
 *
 * See: {@link RecyclerView.Adapter}
 */
public class MessageViewAdapter extends RecyclerView.Adapter<MessageViewAdapter.ViewHolder> {

    private final SortedList<Message> values;

    public MessageViewAdapter(List<Message> items) {
        values = new SortedList<Message>(Message.class, new SortedList.Callback<Message>() {
            @Override
            public int compare(Message o1, Message o2) {
                // handles the sort order of the list
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
        // adds all the specified items to the list
        values.addAll(items);
    }

    /**
     * Add a Message to the list displayed to the user
     * @param message Message to add
     * @return int index that the Message was added
     */
    public int addMessage(Message message) {
        int index = values.add(message);
        Log.i(RadMQTTDemoApplication.LOG_TAG, "Incoming message: " + message.getMessage() + " added at position " + index);
        return index;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // This method is called to create a ViewHolder from the fragment XML.
        // It is called before onBindViewHolder (see method below)
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        // This method sets the datetime and message in the ViewHolder with the Message data from the
        // specified index in the SortedList.
        // If the user scrolls up and down a long list, ViewHolders are created and destroyed
        holder.item = values.get(position);
        holder.dateTimeView.setText(DateFormat.getDateTimeInstance().format(holder.item.getDatetime()));
        holder.messageView.setText(holder.item.getMessage());
    }

    @Override
    public int getItemCount() {
        return values.size();
    }

    /**
     * ViewHolder contains Message data in a format that is displayed to the user
     */
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
