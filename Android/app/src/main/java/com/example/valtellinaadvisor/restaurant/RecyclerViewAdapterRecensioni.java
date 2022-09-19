package com.example.valtellinaadvisor.restaurant;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.valtellinaadvisor.R;
import com.example.valtellinaadvisor.data.Recensione;

import java.util.ArrayList;

public class RecyclerViewAdapterRecensioni extends RecyclerView.Adapter<RecyclerViewAdapterRecensioni.MyViewHolder>{
    private ArrayList<Recensione> elencoRecensioni;
    private RecyclerView recyclerView;
    private GoToListener goToListener;
    private DeleteListener deleteListener;
    private boolean nomeRistorante_flag = false;

    public RecyclerViewAdapterRecensioni(ArrayList<Recensione> elencoRecensioni, RecyclerView recyclerView){
        this.elencoRecensioni = elencoRecensioni;
        this.recyclerView = recyclerView;
    }

    public void setNomeRistoranteVisible(boolean b){
        nomeRistorante_flag = b;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView user;
        private TextView comment;
        private TextView date;
        private RatingBar rating;
        private ImageView img;
        private TextView nomeRistorante;
        private ImageButton goTo;
        private ImageButton delete;

        public MyViewHolder(final View view){
            super(view);
            date = view.findViewById(R.id.date);
            user = view.findViewById(R.id.user);
            comment = view.findViewById(R.id.comment);
            rating = view.findViewById(R.id.rating_comment);
            img = view.findViewById(R.id.img);
            nomeRistorante = view.findViewById(R.id.ristorante);
            goTo = view.findViewById(R.id.go_to);
            goTo.setOnClickListener(this);
            delete = view.findViewById(R.id.delete);
            delete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(view.equals(goTo)) {
                if (goToListener != null) goToListener.onGoTo(itemView, getAdapterPosition());
            }
            else if(view.equals(delete)){
                if (deleteListener != null) deleteListener.onDelete(itemView, getAdapterPosition(), rating.getRating());
            }
        }
    }

    @NonNull
    @Override
    public RecyclerViewAdapterRecensioni.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_recensione, parent , false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapterRecensioni.MyViewHolder holder, int position) {
        Recensione r = elencoRecensioni.get(position);
        holder.date.setText(r.getData());
        holder.comment.setText(r.getCommento());
        holder.rating.setRating((float)r.getVoto());
        if(nomeRistorante_flag){
            holder.user.setVisibility(View.GONE);
            holder.img.setVisibility(View.GONE);
            holder.nomeRistorante.setVisibility(View.VISIBLE);
            holder.nomeRistorante.setText(r.getNomeRistorante());
            holder.goTo.setVisibility(View.VISIBLE);
            holder.delete.setVisibility(View.VISIBLE);
        }
        else {
            holder.user.setText(r.getUsername());
            holder.img.setColorFilter(Color.parseColor(r.getColore()), PorterDuff.Mode.MULTIPLY);
        }
    }

    @Override
    public int getItemCount() {
        return elencoRecensioni.size();
    }

    public void deleteItem(int position){
        elencoRecensioni.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, elencoRecensioni.size());
    }

    public void addItem(Recensione r){
        elencoRecensioni.add(r);
        notifyDataSetChanged();
    }

    public void setElencoRecensioni(ArrayList<Recensione> elencoRecensioni, boolean animation){
        this.elencoRecensioni = elencoRecensioni;
        notifyDataSetChanged();
        if(animation)
            recyclerView.scheduleLayoutAnimation();
    }

    public void setGoToListener(GoToListener goToListener) {
        this.goToListener = goToListener;
    }

    public interface GoToListener {
        void onGoTo(View view, int position);
    }

    public void setDeleteListener(DeleteListener deleteListener) {
        this.deleteListener = deleteListener;
    }

    public interface DeleteListener {
        void onDelete(View view, int position, double votoRecensione);
    }

}
