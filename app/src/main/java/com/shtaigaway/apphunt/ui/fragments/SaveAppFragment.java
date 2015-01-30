package com.shtaigaway.apphunt.ui.fragments;

import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.shtaigaway.apphunt.R;
import com.shtaigaway.apphunt.api.AppHuntApiClient;
import com.shtaigaway.apphunt.api.Callback;
import com.shtaigaway.apphunt.api.models.SaveApp;
import com.shtaigaway.apphunt.utils.Constants;
import com.shtaigaway.apphunt.utils.SharedPreferencesHelper;

import retrofit.client.Response;

public class SaveAppFragment extends BaseFragment implements OnClickListener {

    private View view;
    private EditText desc;
    private ApplicationInfo data;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTitle(R.string.save_app);
        data = getArguments().getParcelable(Constants.KEY_DATA);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_save_app, container, false);

        initUI();

        return view;
    }

    private void initUI() {
        TextView title = (TextView) view.findViewById(R.id.title);
        title.setText(data.loadLabel(getActivity().getPackageManager()));

        ImageView icon = (ImageView) view.findViewById(R.id.icon);
        icon.setImageDrawable(data.loadIcon(getActivity().getPackageManager()));

        desc = (EditText) view.findViewById(R.id.description);

        Button save = (Button) view.findViewById(R.id.save);
        save.setOnClickListener(this);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        if (!enter) {
            return AnimationUtils.loadAnimation(getActivity(), R.anim.slide_out_right);
        }

        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.save:

                SaveApp app = new SaveApp();
                app.setDescription(desc.getText().toString());
                app.setPackageName(data.packageName);
                app.setPlatform(Constants.PLATFORM);
                app.setUserId(SharedPreferencesHelper.getStringPreference(getActivity(), Constants.KEY_USER_ID));

                AppHuntApiClient.getClient().saveApp(app, new Callback() {
                    @Override
                    public void success(Object o, Response response) {
                        if (response.getStatus() == 200) {
                            getActivity().getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            Toast.makeText(getActivity(), "The app was successfully saved! :)", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), "Ops! The app wasn't saved! :(", Toast.LENGTH_LONG).show();
                        }
                    }
                });

                break;
        }
    }
}
