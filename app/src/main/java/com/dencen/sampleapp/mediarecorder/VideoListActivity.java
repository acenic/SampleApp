package com.example.android.mediarecorder;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;

public class VideoListActivity extends AppCompatActivity {
    private LinearLayout llContainer;
    private EditText etSearch;
    private ListView lvProducts;

    private ArrayList<Product> mArrayList = new ArrayList<Product>();
    private MyAdapter adapter1;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_list);


        initialize();



        // Add Text Change Listener to EditText
        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call back the Adapter with current character to Filter
                adapter1.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        String path = Environment.getExternalStorageDirectory().toString()+"/Pictures/CameraSample/";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: "+ files.length);
        for (int i = 0; i < files.length; i++)
        {
            mArrayList.add(new Product( path + files[i].getName(), 100));
            Log.d("Files", "FileName:" + files[i].getName());
        }




        adapter1 = new MyAdapter(VideoListActivity.this, mArrayList);
        lvProducts.setAdapter(adapter1);


    }

    private void initialize() {
        etSearch = (EditText) findViewById(R.id.etSearch);
        lvProducts = (ListView)findViewById(R.id.lvVideos);
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();


    }


    // Adapter Class
    public class MyAdapter extends BaseAdapter implements Filterable {

        private ArrayList<Product> mOriginalValues; // Original Values
        private ArrayList<Product> mDisplayedVideos;    // Values to be displayed
        LayoutInflater inflater;

        public MyAdapter(Context context, ArrayList<Product> mProductArrayList) {
            this.mOriginalValues = mProductArrayList;
            this.mDisplayedVideos = mProductArrayList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mDisplayedVideos.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            LinearLayout llContainer;
            TextView tvName,tvPrice;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {

                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.row, null);
                holder.llContainer = (LinearLayout)convertView.findViewById(R.id.llContainer);
                holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
                holder.tvPrice = (TextView) convertView.findViewById(R.id.tvPrice);

                VideoView videoView = (VideoView)convertView.findViewById(R.id.VideoView);
                videoView.setVideoPath(mDisplayedVideos.get(position).video_path);
                videoView.start();


                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvName.setText(mDisplayedVideos.get(position).video_path);
            holder.tvPrice.setText(mDisplayedVideos.get(position).price+"");

            holder.llContainer.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {

                    Toast.makeText(VideoListActivity.this, mDisplayedVideos.get(position).video_path, Toast.LENGTH_SHORT).show();
                }
            });

            return convertView;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint,FilterResults results) {

                    mDisplayedVideos = (ArrayList<Product>) results.values; // has the filtered values
                    notifyDataSetChanged();  // notifies the data with new filtered values
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                    ArrayList<Product> FilteredArrList = new ArrayList<Product>();

                    if (mOriginalValues == null) {
                        mOriginalValues = new ArrayList<Product>(mDisplayedVideos); // saves the original data in mOriginalValues
                    }

                    /********
                     *
                     *  If constraint(CharSequence that is received) is null returns the mOriginalValues(Original) values
                     *  else does the Filtering and returns FilteredArrList(Filtered)
                     *
                     ********/
                    if (constraint == null || constraint.length() == 0) {

                        // set the Original result to return
                        results.count = mOriginalValues.size();
                        results.values = mOriginalValues;
                    } else {
                        constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < mOriginalValues.size(); i++) {
                            String data = mOriginalValues.get(i).video_path;
                            if (data.toLowerCase().startsWith(constraint.toString())) {
                                FilteredArrList.add(new Product(mOriginalValues.get(i).video_path,mOriginalValues.get(i).price));
                            }
                        }
                        // set the Filtered result to return
                        results.count = FilteredArrList.size();
                        results.values = FilteredArrList;
                    }
                    return results;
                }
            };
            return filter;
        }
    }
}

