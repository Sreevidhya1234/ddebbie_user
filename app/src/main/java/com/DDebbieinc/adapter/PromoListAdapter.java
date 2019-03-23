package com.DDebbieinc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.DDebbieinc.R;
import com.DDebbieinc.activity.PromocodeActivity;
import com.DDebbieinc.entity.PromoNotification;

import java.util.ArrayList;

/**
 * Created by appsplanet on 21/1/16.
 */
public class PromoListAdapter extends BaseAdapter {

    private ArrayList<PromoNotification> arrayList;
    private Context context;

    public PromoListAdapter(ArrayList<PromoNotification> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public int getCount() {
        if(arrayList.size()!=0){
            return arrayList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder = new ViewHolder();
        view= LayoutInflater.from(context).inflate(R.layout.promocode_list_item,null);
        viewHolder.txtPromoTitle =(TextView) view.findViewById(R.id.txtPromoTitle);
        viewHolder.txtPromoBody =(TextView) view.findViewById(R.id.txtPromoBody);
        viewHolder.btnDelete = (Button) view.findViewById(R.id.btnDelete);

        viewHolder.imgPromo =(ImageView) view.findViewById(R.id.imgPromo);

        final PromoNotification promoNotification = arrayList.get(i);
        viewHolder.txtPromoTitle.setText(promoNotification.getTitle());
        String body = promoNotification.getPromo_code() +"\n"+ "Valid from " + promoNotification.getImage() + " to "+ promoNotification.getLink();

        viewHolder.txtPromoBody.setText(body);
        viewHolder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(context instanceof PromocodeActivity){
                    ((PromocodeActivity)context).deletePromocode(i);
                }
            }
        });
        return view;
    }

    public class ViewHolder{
        TextView txtPromoTitle, txtPromoBody;
        ImageView imgPromo;
        Button btnDelete;
    }
}
