package in.navedansari.tpoapp.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import in.navedansari.tpoapp.databinding.FragmentAdminBinding;

public class AdminFragment extends Fragment {
    private FragmentAdminBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAdminBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sharedPreferences = getContext().getSharedPreferences("shared_pref", Context.MODE_PRIVATE);
        String year = sharedPreferences.getString("year", "");

        binding.year.setText(year);
        binding.yearButton.setOnClickListener(v->{
            if(binding.year.isEnabled()){
                binding.year.setEnabled(false);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("year", binding.year.getText().toString().trim());
                editor.apply();
                binding.yearButton.setText("Change");
            }else{
                binding.year.setEnabled(true);
                binding.yearButton.setText("Save");
            }
        });
    }
}