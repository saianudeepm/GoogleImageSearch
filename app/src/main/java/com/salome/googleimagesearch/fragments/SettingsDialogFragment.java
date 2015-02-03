package com.salome.googleimagesearch.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.salome.googleimagesearch.R;
import com.salome.googleimagesearch.models.ImageSearchSettings;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsDialogFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsDialogFragment extends DialogFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    
    private Spinner spnrImageSize;
    private Spinner spnrImageColor;
    private Spinner spnrImageType;
    private EditText etSiteFilter;
    private Button btnSubmit;
    private Button btnCancel;
    
    ImageSearchSettings imageSearchSettings;

    private OnFragmentInteractionListener communicator;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsDialogFragment newInstance(String param1, String param2) {
        SettingsDialogFragment fragment = new SettingsDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public SettingsDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings_dialog, container, false);
        //Get the data passed in via bundle here and set the imageSearchsettings
        imageSearchSettings= (ImageSearchSettings)getArguments().getSerializable("ImageSearchSettings");
        setupViews(view);
        getDialog().setTitle("Filter Settings");
        return view;
    }

    
    
    //set up all the views and hook up any listeners
    private void setupViews(View view) {
        spnrImageSize = (Spinner) view.findViewById(R.id.spnrImageSize);
        spnrImageColor = (Spinner) view.findViewById(R.id.spnrImageColor);
        spnrImageType = (Spinner) view.findViewById(R.id.spnrImageType);
        etSiteFilter = (EditText) view.findViewById(R.id.etSiteFilter);
        btnSubmit = (Button) view.findViewById(R.id.btnSubmit);
        btnCancel = (Button) view.findViewById(R.id.btnCancel);
        
        /** Setting up the array adapter for spnrImageSize */
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> imageSizeAdapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.image_sizes_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        imageSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spnrImageSize.setAdapter(imageSizeAdapter);


        /** Setting up the array adapter for spnrImageColor */
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> imageColorAdapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.image_colors_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        imageColorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spnrImageColor.setAdapter(imageColorAdapter);

        
        /** Setting up the array adapter for spnrImageType */
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> imageTypeAdapter = ArrayAdapter.createFromResource(view.getContext(),
                R.array.image_types_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        imageTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spnrImageType.setAdapter(imageTypeAdapter);

        
        // Set Default Selections from the values passed in to the bundle
        spnrImageSize.setSelection(imageSearchSettings.size);
        spnrImageColor.setSelection(imageSearchSettings.color);
        spnrImageType.setSelection(imageSearchSettings.type);
        etSiteFilter.setText(imageSearchSettings.site);
        
        // Hook up the button handler for the Submit button

        
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(),"Updating the search",Toast.LENGTH_LONG).show();
                // Update Image Search settings
                imageSearchSettings = new ImageSearchSettings();
                imageSearchSettings.size = spnrImageSize.getSelectedItemPosition();
                imageSearchSettings.color = spnrImageColor.getSelectedItemPosition();
                imageSearchSettings.type = spnrImageType.getSelectedItemPosition();
                imageSearchSettings.site = etSiteFilter.getText().toString();
                communicator.onSettingsSubmit(imageSearchSettings);
                dismiss();

            }
        });
        
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        
        
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            communicator = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        communicator = null;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //register listener for handling on submit event
        /*
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                communicator.onSettingsSubmit(view);
            }
        });
        */
        
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onSettingsSubmit(ImageSearchSettings settings);
    }

}
