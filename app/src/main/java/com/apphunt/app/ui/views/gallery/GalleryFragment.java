package com.apphunt.app.ui.views.gallery;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.apphunt.app.R;
import com.squareup.picasso.Picasso;

public class GalleryFragment extends Fragment {
    public static GalleryFragment newInstance(String imageUrl) {
        GalleryFragment fragment = new GalleryFragment();
        Bundle bundle = new Bundle();
        bundle.putString("URL", imageUrl);
        fragment.setArguments(bundle);

        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String url = getArguments().getString("URL").replace("=h310", "");
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        ImageView iv = (ImageView) view.findViewById(R.id.image_view);
        Picasso.with(getActivity()).load(url).into(iv);
        return view;
    }
}
