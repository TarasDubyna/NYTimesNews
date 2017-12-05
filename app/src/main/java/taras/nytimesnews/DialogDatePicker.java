package taras.nytimesnews;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.Calendar;

public class DialogDatePicker extends DialogFragment {

    private SeekBar seekBarYear;
    private SeekBar seekBarMonth;

    private TextView seekBarYearText;
    private TextView seekBarMonthText;

    int year;
    int month;

    public DialogDatePicker() { /*empty*/ }

    /** creates a new instance of PropDialogFragment */
    public static DialogDatePicker newInstance() {
        return new DialogDatePicker();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //getting proper access to LayoutInflater is the trick. getLayoutInflater is a                   //Function
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final View view = inflater.inflate(R.layout.dialog_date_picker, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        final String monthArray[] = getActivity().getResources().getStringArray(R.array.month_array);

        seekBarYear = view.findViewById(R.id.seekBarYear);
        seekBarMonth = view.findViewById(R.id.seekBarMonth);

        seekBarMonthText = view.findViewById(R.id.seekBarTextMonth);
        seekBarYearText = view.findViewById(R.id.seekBarTextYear);

        int maxValueYear = Calendar.getInstance().get(Calendar.YEAR) - 1851;
        int maxValueMonth = monthArray.length - 1;

        seekBarMonth.setProgress(Calendar.getInstance().get(Calendar.MONTH));
        seekBarMonthText.setText("Month: " + monthArray[Calendar.getInstance().get(Calendar.MONTH)]);
        month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        seekBarYear.setProgress(Calendar.getInstance().get(Calendar.YEAR));
        seekBarYearText.setText("Year: " + maxValueYear);
        year = Calendar.getInstance().get(Calendar.YEAR);


        seekBarYear.setMax(maxValueYear);
        seekBarMonth.setMax(maxValueMonth);

        seekBarYear.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                year = progress + 1851;
                seekBarYearText.setText("Year: " + year);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBarMonth.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                year = progress + 1;
                seekBarMonthText.setText("Month: " + monthArray[year - 1]);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        builder.setView(view);

        builder.setTitle(getActivity().getString(R.string.selec_date))
                .setPositiveButton(getActivity().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((MainActivity) view.getContext()).getArchiveRequests(year, month);
                    }
                })
                .setNegativeButton(getActivity().getString(R.string.cancel), null);
        return builder.create();
    }
}