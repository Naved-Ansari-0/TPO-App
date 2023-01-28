package com.example.candidateblocking;

import android.annotation.SuppressLint;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CandidateRecViewAdapter extends RecyclerView.Adapter<CandidateRecViewAdapter.ViewHolder>{

    private ArrayList<Candidate> candidates = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.candidate_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {


        holder.textCountNo.setText((CharSequence) candidates.get(position).getCountNo());
        holder.textName.setText((CharSequence) candidates.get(position).getName());
        holder.textRollNo.setText((CharSequence) candidates.get(position).getRollNo());
        holder.textBranch.setText((CharSequence) candidates.get(position).getBranch());

        if(candidates.get(position).getGender().equals("Male")){
            holder.genderIcon.setImageResource(R.drawable.gender_male_icon);
        }else{
            holder.genderIcon.setImageResource(R.drawable.gender_female_icon);
        }
        holder.textPhoneNo.setText((CharSequence) candidates.get(position).getPhoneNo());
        holder.textEmail.setText((CharSequence) candidates.get(position).getEmail());
        String packages = candidates.get(position).getPackages();
        String companies = candidates.get(position).getCompanies();

        if(candidates.get(position).getPhoneNo().equals("")){
            holder.textPhoneNo.setVisibility(View.GONE);
            holder.phoneIcon.setVisibility(View.GONE);
        }
        if(candidates.get(position).getEmail().equals("")){
            holder.textEmail.setVisibility(View.GONE);
            holder.emailIcon.setVisibility(View.GONE);
        }

        String[] packagesList = packages.split("/", 0);
        String[] companiesList = companies.split("/", 0);
        List<Pair<Float, String>> packagesCompaniesList = new ArrayList<Pair<Float, String>>();
        for(int i=0; i<packagesList.length; i++){
            String t = packagesList[i].replace("+","");
            Float p = Float.parseFloat(t);
            packagesCompaniesList.add(new Pair<>(p, companiesList[i]));
        }
        String packagesCompanies = "";
        packagesCompanies += packagesCompaniesList.get(0).first + " (" + packagesCompaniesList.get(0).second + ")";
        for(int i=1; i<packagesList.length; i++){
            packagesCompanies += "\n";
            packagesCompanies += packagesCompaniesList.get(i).first + " (" + packagesCompaniesList.get(i).second + ")";
        }

      holder.textPackages.setText(packagesCompanies);
      holder.textCompanies.setText("");
      holder.textCompanies.setVisibility(View.GONE);
        if(candidates.get(position).getEmail().equals("") &&
                candidates.get(position).getPhoneNo().equals("")
        ) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.textPackages.getLayoutParams();
            layoutParams.addRule(RelativeLayout.BELOW, holder.textCountNo.getId());
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, holder.cardView.getId());
            holder.textPackages.setLayoutParams(layoutParams);
        }

    }

    @Override
    public int getItemCount() {
        return candidates.size();
    }

    public void setCandidates(ArrayList<Candidate> candidates) {
        this.candidates.clear();
        this.candidates = candidates;
        notifyDataSetChanged();
    }

    public CandidateRecViewAdapter() {
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView textName, textRollNo, textBranch, textGender, textPhoneNo, textEmail, textPackages, textCompanies,  textCountNo;
        private ImageView genderIcon, phoneIcon, emailIcon;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            textName = itemView.findViewById(R.id.textName);
            textRollNo = itemView.findViewById(R.id.textRollNo);
            textBranch = itemView.findViewById(R.id.textBranch);
            textGender = itemView.findViewById(R.id.textGender);
            textPhoneNo = itemView.findViewById(R.id.textPhoneNo);
            textEmail = itemView.findViewById(R.id.textEmail);
            textPackages = itemView.findViewById(R.id.textPackages);
            textCompanies = itemView.findViewById(R.id.textCompanies);
            textCountNo = itemView.findViewById(R.id.candidateCountNo);
            genderIcon = itemView.findViewById(R.id.genderIcon);
            phoneIcon = itemView.findViewById(R.id.phoneIcon);
            emailIcon = itemView.findViewById(R.id.emailIcon);
            cardView = itemView.findViewById(R.id.parent);
        }
    }

}
