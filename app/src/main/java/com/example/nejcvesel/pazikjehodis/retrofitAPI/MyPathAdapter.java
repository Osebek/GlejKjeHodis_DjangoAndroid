package com.example.nejcvesel.pazikjehodis.retrofitAPI;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Point;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.nejcvesel.pazikjehodis.LocationDetailFragment;
import com.example.nejcvesel.pazikjehodis.MainActivity;
import com.example.nejcvesel.pazikjehodis.R;
import com.example.nejcvesel.pazikjehodis.retrofitAPI.Models.Location;
import com.example.nejcvesel.pazikjehodis.retrofitAPI.Models.Path;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
/**
 * Created by nejcvesel on 13/02/17.
 */

public class MyPathAdapter extends RecyclerView.Adapter<MyPathAdapter.ViewHolder> {
    List<Path> mItems;
    Context context;


    public MyPathAdapter(Context context) {
        super();
        this.context = context;
        mItems = new ArrayList<Path>();
    }

    public void addData(Path pth) {
        mItems.add(pth);
        notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.recycler_view_paths, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);



        return viewHolder;

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        Path path = mItems.get(i);
        System.out.println(path.getId());

        viewHolder.pathOwner.setText(path.getOwner());
        viewHolder.pathName.setText(path.getName());
        viewHolder.pathCity.setText(path.getCity());
        viewHolder.pathDescription.setText(path.getDescription());
        viewHolder.pathID.setText(Integer.toString(path.getId()));
        List<Integer> locs = path.getPathLocations();
        viewHolder.pathLocations.setText(locs.toString());
        viewHolder.numOfPathLocations.setText(String.valueOf(locs.size()));



    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public TextView pathOwner;
        public TextView pathName;
        public TextView pathCity;
        public TextView pathDescription;
        public TextView pathID;
        public TextView pathLocations;
        public TextView numOfPathLocations;


        public ViewHolder(final View itemView) {
            super(itemView);
            pathOwner = (TextView) itemView.findViewById(R.id.path_owner);
            pathName = (TextView) itemView.findViewById(R.id.path_name);
            pathCity = (TextView) itemView.findViewById(R.id.path_city);
            pathDescription = (TextView) itemView.findViewById(R.id.path_description);
            pathID = (TextView) itemView.findViewById(R.id.path_id);
            pathLocations = (TextView) itemView.findViewById(R.id.path_locations);
            numOfPathLocations = (TextView) itemView.findViewById(R.id.path_num_of_locations);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println(pathID.getText());

                   /* Location loc = new Location();
                    loc.setTitle(title.getText().toString());
                    loc.setText(text.getText().toString());
                    loc.setName(name.getText().toString());
                    loc.setPicture(pictureURL.getText().toString());
                    loc.setId(Integer.valueOf(locationID.getText().toString()));

                    Fragment fragment = LocationDetailFragment.newInstance(loc);
                    FragmentManager fragmentManager = ((FragmentActivity)context).getFragmentManager();
                    FragmentTransaction fragmentTransaction =
                            fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame,fragment,"LocationDetail");
                    fragmentTransaction.addToBackStack("LocationDetail");
                    fragmentTransaction.commit();*/
                    //Intent intent = new Intent(itemView.getContext(), MapsActivity.class);
                    //itemView.getContext().startActivity(intent);

                }
            });

            ImageView icon = (ImageView) itemView.findViewById(R.id.path_icon);
            icon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MainActivity main = (MainActivity) context;
                    main.showLocationOnMap(v,(String)pathLocations.getText());

                }
            });

        }
    }
}







