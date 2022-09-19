package com.example.valtellinaadvisor.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.valtellinaadvisor.R;
import com.example.valtellinaadvisor.data.Ristorante;

import java.util.ArrayList;
import java.util.Locale;

public class RecyclerViewAdapterRistoranti extends RecyclerView.Adapter<RecyclerViewAdapterRistoranti.MyViewHolder>{
    private ArrayList<Ristorante> elencoRistoranti;
    private RecyclerView recyclerView;
    private ItemClickListener mClickListener;
    private OnFavoriteClickListener onFavoriteClickListener;

    public RecyclerViewAdapterRistoranti(ArrayList<Ristorante> elencoRistoranti, RecyclerView recyclerView){
        this.elencoRistoranti = elencoRistoranti;
        this.recyclerView = recyclerView;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView nome;
        private TextView indirizzo;
        private TextView rating;
        private ImageButton favorite;

        public MyViewHolder(final View view){
            super(view);
            nome = view.findViewById(R.id.nome);
            indirizzo = view.findViewById(R.id.indirizzo);
            rating = view.findViewById(R.id.rating);
            favorite = view.findViewById(R.id.favorite);
            favorite.setOnClickListener(this);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view instanceof ImageButton) {
                if (onFavoriteClickListener != null) onFavoriteClickListener.onFavoriteClick((ImageButton)view, getAdapterPosition(), (int)view.getTag());
            }
            else {
                if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            }
        }
    }

    @NonNull
    @Override
    public RecyclerViewAdapterRistoranti.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_ristorante, parent , false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterRistoranti.MyViewHolder holder, int position) {
        Ristorante r = elencoRistoranti.get(position);
        holder.nome.setText(r.getNome());
        holder.indirizzo.setText(r.getIndirizzo()+" - "+r.getCitta().getNome());
        holder.rating.setText(String.format(Locale.US, "%.1f", r.getRating()));
        holder.favorite.setTag(r.getIdRistorante());
        if(r.isFavorite())
            holder.favorite.setImageResource(R.drawable.ic_baseline_favorite_red_24);
        else
            holder.favorite.setImageResource(R.drawable.ic_baseline_favorite_border_24);
    }

    @Override
    public int getItemCount() {
        return elencoRistoranti.size();
    }

    public void deleteItem(int position){
        elencoRistoranti.remove(position);
        notifyDataSetChanged();
    }

    public void addItem(Ristorante r){
        elencoRistoranti.add(r);
        notifyDataSetChanged();
    }

    public void setElencoRistoranti(ArrayList<Ristorante> elencoRistoranti, boolean animation){
        this.elencoRistoranti = elencoRistoranti;
        notifyDataSetChanged();
        if(animation)
            recyclerView.scheduleLayoutAnimation();
    }

    void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(ImageButton favoriteButton, int position, int idRistorante);
    }

    public void setOnFavoriteClickListener(OnFavoriteClickListener onFavoriteClickListener){
        this.onFavoriteClickListener = onFavoriteClickListener;
    }
}
