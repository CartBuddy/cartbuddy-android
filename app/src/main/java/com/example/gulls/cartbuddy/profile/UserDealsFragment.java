package com.example.gulls.cartbuddy.profile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.gulls.cartbuddy.Deal;
import com.example.gulls.cartbuddy.DealAdapter;
import com.example.gulls.cartbuddy.HttpClient;
import com.example.gulls.cartbuddy.NearbyActivity;
import com.example.gulls.cartbuddy.R;
import com.example.gulls.cartbuddy.User;
import com.example.gulls.cartbuddy.UserSession;
import com.example.gulls.cartbuddy.ViewSingleDealActivity;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserDealsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserDealsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserDealsFragment extends Fragment {
    public static final String TITLE = "My Deals";

    private static final String TAG = UserDealsFragment.class.getName();

    private OkHttpClient httpClient = HttpClient.getClient();
    private User user = UserSession.getSession().getActiveUser();
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";

    private String baseUrl;
    private List<Deal> userDeals = new ArrayList<>();

    private DealAdapter dealAdapter;

    private String mParam1;
    private String mParam2;

    private View view;

    private OnFragmentInteractionListener mListener;

    public UserDealsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment UserDealsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserDealsFragment newInstance() {
        UserDealsFragment fragment = new UserDealsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, "sample");
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }

        baseUrl = getString(R.string.base_url);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_user_deals, container, false);

        // set list view
        ListView listView = view.findViewById(R.id.list_view);
        dealAdapter = new DealAdapter(getContext(), (ArrayList)userDeals);
        listView.setAdapter(dealAdapter);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Request request = new Request.Builder()
                .url(baseUrl + "/deals?user_id=" + user.id)
                .build();
        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try (ResponseBody responseBody = response.body()) {
                    if (!response.isSuccessful()) {
                        throw new IOException("Unexpected response code: " + response.code());
                    }
                    String dealsStr = responseBody.string();
                    Log.d(TAG, dealsStr);
                    Moshi moshi = new Moshi.Builder().build();
                    Type type = Types.newParameterizedType(List.class, Deal.class);
                    JsonAdapter<List<Deal>> jsonAdapter = moshi.adapter(type);
                    userDeals = jsonAdapter.fromJson(dealsStr);
                    for (Deal deal : userDeals) {
                        if (deal.photoUrls != null) {
                            deal.photoUrl = deal.photoUrls[0];
                        }
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ListView listView = view.findViewById(R.id.list_view);
                            dealAdapter = new DealAdapter(getContext(), (ArrayList)userDeals);
                            listView.setAdapter(dealAdapter);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View v,
                                                        int position, long id) {
                                    String dealId = userDeals.get(position).id;
                                    Intent intent = new Intent(getContext(), ViewSingleDealActivity.class);
                                    intent.putExtra("ID", dealId);
                                    startActivity(intent);
                                }
                            });
                        }
                    });


//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            ((TextView)findViewById(R.id.num_deals)).setText(userDeals.size() + " Deals Posted");
//                        }
//                    });
                }
            }
        });
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
