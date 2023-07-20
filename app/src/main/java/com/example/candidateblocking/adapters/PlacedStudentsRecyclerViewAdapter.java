package com.example.candidateblocking.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.candidateblocking.R;
import com.example.candidateblocking.models.PlacedStudentModel;

import java.util.ArrayList;


public class PlacedStudentsRecyclerViewAdapter extends RecyclerView.Adapter<PlacedStudentsRecyclerViewAdapter.ViewHolder>{

    private ArrayList<PlacedStudentModel> placedStudents = new ArrayList<>();

    public PlacedStudentsRecyclerViewAdapter() {
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.placed_student_list_item, parent, false);
        return new ViewHolder(view);
    }

    public int getItemCount() {
        return placedStudents.size();
    }

    public void setPlacedStudents(ArrayList<PlacedStudentModel> placedStudents) {
        this.placedStudents.clear();
        this.placedStudents = placedStudents;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textName, textRollNo, textBranch, textPhoneNo, textEmail, placementDetail,  textSerialNo;
        private ImageView genderIcon, phoneIcon, emailIcon;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            textName = itemView.findViewById(R.id.textName);
            textRollNo = itemView.findViewById(R.id.textRollNo);
            textBranch = itemView.findViewById(R.id.textBranch);
            textPhoneNo = itemView.findViewById(R.id.textPhoneNo);
            textEmail = itemView.findViewById(R.id.textEmail);
            placementDetail = itemView.findViewById(R.id.placementDetail);
            textSerialNo = itemView.findViewById(R.id.textCountNo);
            genderIcon = itemView.findViewById(R.id.genderIcon);
            phoneIcon = itemView.findViewById(R.id.phoneIcon);
            emailIcon = itemView.findViewById(R.id.emailIcon);

            phoneIcon.setOnLongClickListener(v->{
                String phoneNo = textPhoneNo.getText().toString();
                if(phoneNo.equals("") || phoneNo.equals("hidden"))
                    return true;
                copyToClipboard(phoneNo,"Phone no copied to clipboard", itemView.getContext());
                return true;
            });

            emailIcon.setOnLongClickListener(v->{
                String email = textEmail.getText().toString();
                if(email.equals("") || email.equals("hidden"))
                    return true;
                copyToClipboard(email,"Email copied to clipboard", itemView.getContext());
                return true;
            });

        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.textSerialNo.setText(placedStudents.get(position).getSerialNo());
        holder.textName.setText(placedStudents.get(position).getName());
        holder.textRollNo.setText(placedStudents.get(position).getRollNo());
        holder.textBranch.setText(placedStudents.get(position).getBranch());

        if(placedStudents.get(position).getGender().equals("Male")){
            holder.genderIcon.setImageResource(R.drawable.gender_male_icon);
        }else{
            holder.genderIcon.setImageResource(R.drawable.gender_female_icon);
        }

        if(placedStudents.get(position).getPhoneNo().equals("")){
//            holder.textPhoneNo.setVisibility(View.GONE);
//            holder.phoneIcon.setVisibility(View.GONE);
            holder.textPhoneNo.setText("hidden");
        }else{
            holder.textPhoneNo.setText(placedStudents.get(position).getPhoneNo());
        }
        if(placedStudents.get(position).getEmail().equals("")){
//            holder.textEmail.setVisibility(View.GONE);
//            holder.emailIcon.setVisibility(View.GONE);
            holder.textEmail.setText("hidden");
        }else{
            holder.textEmail.setText(placedStudents.get(position).getEmail());
        }

        ArrayList<Float> packages = placedStudents.get(position).getPackages();
        ArrayList<String> companies = placedStudents.get(position).getCompanies();

        String placementDetail = "";
        if(packages.size()>0){
            placementDetail += packages.get(0) + " (" + companies.get(0) + ")";
            for(int i=1; i<packages.size(); i++)
                placementDetail += "\n" + packages.get(i) + " (" + companies.get(i) + ")";
        }
        holder.placementDetail.setText(placementDetail);

    }

    private void copyToClipboard(String text, String message, Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", text);
        clipboardManager.setPrimaryClip(clip);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


}
