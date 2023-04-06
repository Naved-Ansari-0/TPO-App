package com.example.candidateblocking;

import android.annotation.SuppressLint;
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
public class PlacedStudentsRecyclerViewAdapter extends RecyclerView.Adapter<PlacedStudentsRecyclerViewAdapter.ViewHolder>{

    private ArrayList<PlacedStudentModel> placedStudents = new ArrayList<>();

    public PlacedStudentsRecyclerViewAdapter() {
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.placed_student_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.textCountNo.setText(placedStudents.get(position).getCountNo());
        holder.textName.setText(placedStudents.get(position).getName());
        holder.textRollNo.setText(placedStudents.get(position).getRollNo());
        holder.textBranch.setText(placedStudents.get(position).getBranch());

        if(placedStudents.get(position).getGender().equals("Male")){
            holder.genderIcon.setImageResource(R.drawable.gender_male_icon);
        }else{
            holder.genderIcon.setImageResource(R.drawable.gender_female_icon);
        }

        if(placedStudents.get(position).getPhoneNo().equals("")){
            holder.textPhoneNo.setVisibility(View.GONE);
            holder.phoneIcon.setVisibility(View.GONE);
        }else{
            holder.textPhoneNo.setText(placedStudents.get(position).getPhoneNo());
        }
        if(placedStudents.get(position).getEmail().equals("")){
            holder.textEmail.setVisibility(View.GONE);
            holder.emailIcon.setVisibility(View.GONE);
        }else{
            holder.textEmail.setText(placedStudents.get(position).getEmail());
        }

        if(placedStudents.get(position).getEmail().equals("") &&
                placedStudents.get(position).getPhoneNo().equals("")
        ) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.textPackages.getLayoutParams();
            layoutParams.addRule(RelativeLayout.BELOW, holder.textCountNo.getId());
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, holder.placedStudentCardView.getId());
            holder.textPackages.setLayoutParams(layoutParams);
        }

        holder.textCompanies.setText("");
        holder.textCompanies.setVisibility(View.GONE);

        ArrayList<Float> packages = placedStudents.get(position).getPackages();
        ArrayList<String> companies = placedStudents.get(position).getCompanies();

        String placementDetail = "";
        placementDetail += packages.get(0) + " (" + companies.get(0) + ")";
        for(int i=1; i<packages.size(); i++){
            placementDetail += "\n";
            placementDetail += packages.get(i) + " (" + companies.get(i) + ")";
        }
        holder.textPackages.setText(placementDetail);

    }

    @Override
    public int getItemCount() {
        return placedStudents.size();
    }

    public void setPlacedStudents(ArrayList<PlacedStudentModel> placedStudents) {
        this.placedStudents.clear();
        this.placedStudents = placedStudents;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView textName, textRollNo, textBranch, textGender, textPhoneNo, textEmail, textPackages, textCompanies,  textCountNo;
        private ImageView genderIcon, phoneIcon, emailIcon;
        private CardView placedStudentCardView;

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
            textCountNo = itemView.findViewById(R.id.textCountNo);
            genderIcon = itemView.findViewById(R.id.genderIcon);
            phoneIcon = itemView.findViewById(R.id.phoneIcon);
            emailIcon = itemView.findViewById(R.id.emailIcon);
            placedStudentCardView = itemView.findViewById(R.id.placedStudentCardView);
        }
    }

}
