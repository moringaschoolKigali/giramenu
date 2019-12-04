package rw.datasystems.mealorder.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import rw.datasystems.mealorder.Activitiy.AnythingElseListActivity;
import rw.datasystems.mealorder.Activitiy.ContinueActivity;
import rw.datasystems.mealorder.Activitiy.ExcludeListActivity;
import rw.datasystems.mealorder.Activitiy.SideDishActivity;
import rw.datasystems.mealorder.UI.ClickNumber.ClickNumberPickerView;
import rw.datasystems.mealorder.Util.Constants;
import rw.datasystems.mealorder.R;
import rw.datasystems.mealorder.UI.CustomFontButton;
import android.widget.ImageView;
import rw.datasystems.mealorder.UI.CustomFontText;

import com.squareup.picasso.Picasso;

import java.util.List;

//import pl.polak.clicknumberpicker.ClickNumberPickerView;
import rw.datasystems.mealorder.adapters.PriceAdapter;
import rw.datasystems.mealorder.models.Component;
import rw.datasystems.mealorder.models.Items;
import rw.datasystems.mealorder.models.OrderItem;
import rw.datasystems.mealorder.models.Price;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ItemDetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ItemDetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemDetailFragment extends Fragment {


    private OnFragmentInteractionListener mListener;

    private static Items mItem;
    private String cat;
    private int item;
    private Activity activity;
    private static RecyclerView price_list;
    private static CustomFontText choose;
    private static ClickNumberPickerView clickNumberPickerView;
    private static CustomFontButton addtopplate;
    public static CustomFontButton back;

    private static LayoutInflater inflater;
    private static List<Component> components;
    private static List<Price> prices;
    private CustomFontText price;

    public static final String ARG_ITEM_ID = "item_id";
    public static final String ARG_CAT_ID = "cat_id";
    public static final String ARG_CONFIRM =  "confirm";

    public ItemDetailFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ItemDetailFragment newInstance() {
        ItemDetailFragment fragment = new ItemDetailFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ItemDetailFragment.ARG_ITEM_ID)) {

            cat = getArguments().getString(ItemDetailFragment.ARG_CAT_ID);
            item = getArguments().getInt(ItemDetailFragment.ARG_ITEM_ID);

            mItem = new Items();
            if(Constants.allitems != null)
                try {
                    mItem = Constants.allitems.get(cat).get(item);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            activity = this.getActivity();
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Constants.controlPicker = false;
        this.inflater = inflater;
        final View rootView = inflater.inflate(R.layout.fragment_new_item_details, container, false);

        prices = mItem.prices;

        price_list = ((RecyclerView) rootView.findViewById(R.id.size_list));
        price_list.setAdapter(new PriceAdapter(getActivity(), prices));
        price_list.setLayoutManager(new LinearLayoutManager(getActivity()));

        choose = ((CustomFontText) rootView.findViewById(R.id.choosetext));
        price = ((CustomFontText) rootView.findViewById(R.id.price));

        //price.setText(prices.get(0).getPrice() + " RWF" );

        clickNumberPickerView = ((ClickNumberPickerView) rootView.findViewById(R.id.quantitypicker));
        addtopplate = ((CustomFontButton) rootView.findViewById(R.id.addtopplate));

        if (Constants.lang == 1) {
            choose.setText("Choose your option");
            addtopplate.setText("OK, Next");
        } else {
            choose.setText("Choisissez votre option");
            addtopplate.setText("Oui, continuer");

        }


        ImageView itemimage = (ImageView) rootView.findViewById(R.id.item_image);
        CustomFontText name = (CustomFontText) rootView.findViewById(R.id.name);
        CustomFontText desc = (CustomFontText) rootView.findViewById(R.id.description);

        String url = mItem.cover;

        if (url != null)
            url = Constants.baseUrl + "/cover/" + url.replace(".jpg", "") + "?type=3";

        Picasso.with(getContext())
                .load(url)
                .placeholder(R.drawable.thumb)
                .error(R.drawable.thumb)
                .into(itemimage);

        name.setText(mItem.getName());
        desc.setText(mItem.getDescription());

        components = mItem.components;
        Constants.currentItem = mItem;

        back = (CustomFontButton) rootView.findViewById(R.id.back);

        if(Constants.lang == 2)
        {
            back.setText("arrière");
        }
        else
        {
            back.setText("back");
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });


        return rootView;
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public static void change(final Context context, final int position) {
        price_list.setVisibility(View.GONE);

        try {
            Thread.sleep(600);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (price_list.getVisibility() == View.GONE) {

            addtopplate.setVisibility(View.VISIBLE);


        } else {
            price_list.setVisibility(View.GONE);
            addtopplate.setVisibility(View.VISIBLE);

        }

        addtopplate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                OrderItem oitem = new OrderItem();

                oitem.current_price = Float.parseFloat(prices.get(position).price);
                oitem.itemclass = prices.get(position).getName();

                oitem.qnty = (int) clickNumberPickerView.getValue();
                oitem.item_name = mItem.name;
                oitem.order_item_id = mItem._id;

                oitem.item = mItem;

                if(mItem.components == null || mItem.components.size() == 0)
                {
                    Constants.isCategories = true;

                    if(mItem.side_dishes != null && mItem.side_dishes.size() > 0)
                    {
                        Intent intent = new Intent(context, SideDishActivity.class);
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, mItem._id);
                        Constants.pendingOrderItem = oitem ;
                      //  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }

                    else if(mItem.related != null && mItem.related.size() > 0) {

                        Intent intent = new Intent(context, AnythingElseListActivity.class);
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, mItem._id);

                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        Constants.orderitems.add(oitem);
                    }

                    else {
                        context.startActivity(new Intent(context, ContinueActivity.class).putExtra("OrderAdded", true));
                        Constants.orderitems.add(oitem);
                    }

                }
                else
                {

                    Constants.pendingOrderItem = oitem ;
                    context.startActivity(new Intent(context, ExcludeListActivity.class).putExtra("OrderAdded", true));

                }


            }
        });

        if (Constants.lang == 1) {
            choose.setText("Choose the quantity");
            addtopplate.setText("Next");
        } else {
            choose.setText("Choisissez la quantité");
            addtopplate.setText("Oui, continuer");
        }
        clickNumberPickerView.setVisibility(View.VISIBLE);



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
