package com.example.lostpets.Classes;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lostpets.Display_Pet_Fragment;
import com.example.lostpets.HomePageFragment;
import com.example.lostpets.R;

import org.chromium.net.CronetEngine;
import org.chromium.net.CronetException;
import org.chromium.net.UrlRequest;
import org.chromium.net.UrlResponseInfo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DisplayBreedsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DisplayBreedsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private ListView petsListView;
    private String mParam1;
    private String mParam2;
    private CronetEngine.Builder myBuilder;
    private CronetEngine cronetEngine;
    private Executor executor;

    private ArrayList<Breed> list;

    public DisplayBreedsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DisplayBreedsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DisplayBreedsFragment newInstance(String param1, String param2) {
        DisplayBreedsFragment fragment = new DisplayBreedsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myBuilder = new CronetEngine.Builder(getContext());
        cronetEngine = myBuilder.build();
        executor = Executors.newSingleThreadExecutor();
        list=new ArrayList<>();
    }
    public void makeBreedsApiRequest() {
        UrlRequest.Builder requestBuilder = cronetEngine.newUrlRequestBuilder(
                "https://dog.ceo/api/breeds/list/all",
                new BreedsRequestCallback(),
                executor);

        UrlRequest request = requestBuilder.build();
        // here we make the actual api call
        request.start();
    }
    public void updateBreedInfoTextView(String breedData) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject jsonObject = new JSONObject(breedData);
                    JSONObject messageObject = jsonObject.getJSONObject("message");
                    JSONArray breedNames = messageObject.names();
                    // Iterate through breed names
                    if (breedNames != null) {
                        for (int i = 0; i < breedNames.length(); i++) {
                            String breed = breedNames.getString(i);
                            JSONArray subBreeds = messageObject.getJSONArray(breed);


                            if (subBreeds.length() > 0) {
                                for (int j = 0; j < subBreeds.length(); j++) {
                                    Breed breed_sample=new Breed(breed+" "+subBreeds.getString(j));
                                    list.add(breed_sample);
                                }
                            }
                        }
                    }
                    BreedsAdapter adapter = new BreedsAdapter(requireContext(), list);
                    petsListView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    class BreedsRequestCallback extends UrlRequest.Callback {
        private static final String TAG = "EPL498RequestCallback";

        @Override
        public void onResponseStarted(UrlRequest request, UrlResponseInfo info) {
            Log.i(TAG, "onResponseStarted method called.");
            request.read(ByteBuffer.allocateDirect(102400));
        }

        @Override
        public void onReadCompleted(UrlRequest request, UrlResponseInfo info, ByteBuffer byteBuffer) {
            Log.i(TAG, "onReadCompleted method called.");
            // You should keep reading the request until there's no more data.
            byteBuffer.flip();
            byte[] bytes = new byte[byteBuffer.remaining()];
            byteBuffer.get(bytes);
            String response = new String(bytes);

            // Let's see the json result in Logcat
            Log.i("Breeds", response);
            updateBreedInfoTextView(response);

            byteBuffer.clear();
            request.read(byteBuffer);
        }

        @Override
        public void onSucceeded(UrlRequest request, UrlResponseInfo info) {
            Log.i(TAG, "onSucceeded method called.");
        }

        @Override
        public void onFailed(UrlRequest request, UrlResponseInfo info, CronetException error) {
            Log.i(TAG, "onFailed method called.");
        }

        @Override
        public void onRedirectReceived(UrlRequest request, UrlResponseInfo info, String newLocationUrl) throws Exception {
            // do nothing for now
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_display_breeds, container, false);
        petsListView=view.findViewById(R.id.breedsView);
        makeBreedsApiRequest();
        return view;
    }
    private class BreedsAdapter extends ArrayAdapter<Breed> {

        BreedsAdapter(android.content.Context context, ArrayList<Breed> breed_list) {
            super(context, R.layout.list_item_breed, breed_list);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_breed, parent, false);

            }


            Breed petItem = getItem(position);
            TextView breedTextView = convertView.findViewById(R.id.petBreedTextView);
            if (petItem != null) {
                breedTextView.setText(petItem.getBreed_name());
            }

            return convertView;
        }
    }
}