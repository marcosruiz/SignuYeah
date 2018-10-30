package markens.signu.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import markens.signu.R;
import markens.signu.objects.User;

public class UserListCheckboxAdapter extends BaseAdapter {
    private static LayoutInflater inflater = null;
    List<User> users;
    Context myCtx;
    ArrayList<String> usersIdSelected;

    public UserListCheckboxAdapter(Context context, List<User> users) {
        this.users = users;
        myCtx = context;
        usersIdSelected = new ArrayList<>();
        inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        User ue = users.get(position);
        final View view = inflater.inflate(R.layout.user_item_selectable, null);

        TextView userId = (TextView) view.findViewById(R.id.textViewUserId);
        TextView userEmail = (TextView) view.findViewById(R.id.textViewCert);
        TextView userName = (TextView) view.findViewById(R.id.textViewCertDes);
        TextView userLastname = (TextView) view.findViewById(R.id.textViewUserLastname);
        CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkBox);

        userId.setText(ue.getId());
        userEmail.setText(ue.getEmail());
        userName.setText(ue.getName());
        userLastname.setText(ue.getLastname());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkBox);
                checkBox.setChecked(!checkBox.isChecked());
                TextView userId = (TextView) v.findViewById(R.id.textViewUserId);
                if (checkBox.isChecked()) {
                    usersIdSelected.add(userId.getText().toString());
                } else {
                    usersIdSelected.remove(userId.getText().toString());
                }
            }
        });

        return view;
    }

    public ArrayList<String> getUsersIdSelected() {
        return usersIdSelected;
    }
}
