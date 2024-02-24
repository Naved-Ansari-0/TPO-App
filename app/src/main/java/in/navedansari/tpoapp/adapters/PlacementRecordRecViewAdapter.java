package in.navedansari.tpoapp.adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import in.navedansari.tpoapp.R;
import in.navedansari.tpoapp.databinding.PlacementRecordListItemBinding;
import in.navedansari.tpoapp.models.PlacementRecord;

public class PlacementRecordRecViewAdapter extends RecyclerView.Adapter<PlacementRecordRecViewAdapter.ViewHolder> {
    private ArrayList<PlacementRecord> placedStudents = new ArrayList<>();
    public PlacementRecordRecViewAdapter() {
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PlacementRecordListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.placement_record_list_item, parent, false);
        return new ViewHolder(binding);
    }
    public int getItemCount() {
        return placedStudents.size();
    }
    public void setPlacedStudents(ArrayList<PlacementRecord> placedStudents) {
        this.placedStudents.clear();
        this.placedStudents = placedStudents;
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private final PlacementRecordListItemBinding binding;
        public ViewHolder(@NonNull PlacementRecordListItemBinding binding){
            super(binding.getRoot());
            this.binding = binding;
            binding.phoneIcon.setOnLongClickListener(v->{
                String phoneNo = binding.phoneNo.getText().toString();
                copyToClipboard(phoneNo,"Phone no copied to clipboard", itemView.getContext());
                return true;
            });
            binding.emailIcon.setOnLongClickListener(v->{
                String email = binding.email.getText().toString();
                copyToClipboard(email,"Email copied to clipboard", itemView.getContext());
                return true;
            });
        }
        public void bind(PlacementRecord record) {
            binding.setRecord(record);
            binding.executePendingBindings();
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder,int position) {
        PlacementRecord record = placedStudents.get(position);
        holder.bind(record);

        if(record.getGender().toLowerCase().equals("male")){
            holder.binding.genderIcon.setImageResource(R.drawable.gender_male_icon);
        } else if(record.getGender().toLowerCase().equals("female")){
            holder.binding.genderIcon.setImageResource(R.drawable.gender_female_icon);
        }

        String placementDetail = "";
        String company = "";
        String drive = "";
        Float ctc = (float)0.0;
        if(record.getPlacedIn().size()>0){
            company = record.getPlacedIn().get(0).getCompany();
            drive = record.getPlacedIn().get(0).getDrive();
            ctc = record.getPlacedIn().get(0).getCtc();
            placementDetail += company +  " (" + ctc + " lpa) [" + drive + "]";
            for(int i=1; i<record.getPlacedIn().size(); i++) {
                company = record.getPlacedIn().get(i).getCompany();
                drive = record.getPlacedIn().get(i).getDrive();
                ctc = record.getPlacedIn().get(i).getCtc();
                placementDetail += "\n" + company +  " (" + ctc + " lpa) [" + drive + "]";
            }
        }
        holder.binding.placedIn.setText(placementDetail);
    }

    private void copyToClipboard(String text, String message, Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", text);
        clipboardManager.setPrimaryClip(clip);
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
