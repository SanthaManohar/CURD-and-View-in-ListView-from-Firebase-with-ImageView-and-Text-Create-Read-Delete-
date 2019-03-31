package basicandroid.com.firebasenew;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {

    private Context mContext;
    private List<Member> mUploads;
    private OnItemClickListener mListener;

    public ImageAdapter(Context context,List<Member> uploads){
        mContext = context;
        mUploads = uploads;

    }

    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.image_item,parent,false);
        return new ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        Member currentMember = mUploads.get(position);
        holder.textViewName.setText(currentMember.getName());
        Picasso.with(mContext).load(currentMember.getImage()).fit().centerCrop().into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnCreateContextMenuListener,
            MenuItem.OnMenuItemClickListener{

        public TextView textViewName;
        public ImageView imageView;

        public ImageViewHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.text_view_name);
            imageView = itemView.findViewById(R.id.image_view_upload);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);

        }


        @Override
        public void onClick(View view) {

            if(mListener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){
                    mListener.onItemClick(position);
                }
            }

        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Select Action");
            MenuItem doWhatever = contextMenu.add(Menu.NONE,1,1,"Do Whatever");
            MenuItem delete = contextMenu.add(Menu.NONE,2,2,"Delete");

            doWhatever.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if(mListener != null){
                int position = getAdapterPosition();
                if(position != RecyclerView.NO_POSITION){

                    switch (menuItem.getItemId()){
                        case  1 :
                            mListener.onWhateverClick(position);
                            return true;
                        case  2 :
                            mListener.onDeleteClick(position);
                            return true;
                    }

                }
            }
            return false;
        }
    }

    public  interface OnItemClickListener {

        void onItemClick(int position);

        void onWhateverClick(int position);

        void onDeleteClick(int position);


    }

    public void setOnItemClickListener(OnItemClickListener listener){
        mListener = listener;
    }


}
