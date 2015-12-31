package tk.amxinfinity.statisticalanalysis;

import android.app.AlertDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showAboutDialog(MenuItem item) {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.about_message)
                .setTitle(R.string.about_title)
                .setPositiveButton(" Okay! ", null)
                .setCancelable(true);

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    public void showInsDialog(MenuItem item) {
        // 1. Instantiate an AlertDialog.Builder with its constructor
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        // 2. Chain together various setter methods to set the dialog characteristics
        builder.setMessage(R.string.ins_message)
                .setTitle(R.string.ins_title)
                .setPositiveButton(" Okay! ", null)
                .setCancelable(true);

        // 3. Get the AlertDialog from create()
        AlertDialog dialog = builder.create();

        dialog.show();
    }

    public void doThis(MenuItem item){
        Toast.makeText(this, "Press back button to hide the keyboard.", Toast.LENGTH_LONG).show();
    }

    public void doReset(View view) {
        ((EditText)findViewById(R.id.etXi)).setText("");
        ((EditText)findViewById(R.id.etFi)).setText("");
        ((EditText)findViewById(R.id.etCF)).setText("");
        ((EditText)findViewById(R.id.etFiXi)).setText("");
        ((EditText)findViewById(R.id.etXiM)).setText("");
        ((EditText)findViewById(R.id.etXiMd)).setText("");
        ((EditText)findViewById(R.id.etFiXiM)).setText("");
        ((EditText)findViewById(R.id.etFiXiMd)).setText("");
        ((EditText)findViewById(R.id.etMean)).setText("");
        ((EditText)findViewById(R.id.etMd)).setText("");
        ((EditText)findViewById(R.id.etDevM)).setText("");
        ((EditText)findViewById(R.id.etDevMd)).setText("");

        findViewById(R.id.resultContainer).setVisibility(View.GONE);
    }

    public void doCalculate(View view) {
        String t1 = String.valueOf(((EditText) findViewById(R.id.etXi)).getText());
        String[] tmps1 = t1.trim().split("\n");
        String t2 = String.valueOf(((EditText)findViewById(R.id.etFi)).getText());
        String[] tmps2 = t2.trim().split("\n");
        boolean err = false;
        if (tmps1.length != tmps2.length) { err = true; }
        for(int i = 0; i < tmps1.length; i++)
        {
            if (!tmps1[i].matches("[-+]?[0-9]*\\.?[0-9]+")) { err = true; }
        }

        for(int i = 0; i < tmps2.length; i++)
        {
            if (!tmps2[i].matches("[0-9]+")) { err = true; }
        }

        if (err)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            builder.setMessage(R.string.err_message)
                    .setTitle(R.string.err_title)
                    .setPositiveButton(" Okay! ", null)
                    .setCancelable(true);

            AlertDialog dialog = builder.create();

            dialog.show();

            return;
        }

        findViewById(R.id.resultContainer).setVisibility(View.VISIBLE);
        CalculateAndPrint();
        Toast keyboardMsg = Toast.makeText(this, "Press back ( ← ) to hide the keyboard!", Toast.LENGTH_LONG);
        keyboardMsg.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
        keyboardMsg.show();
    }

    double[] Xi, Fi, XiFi, XiDev, FiDev, CF, XiMDev, FiMDev;
    double sum, sum2, sum3, mean, N, mdm, median, mdmedian;
    boolean even;
    String[] tmp1, tmp2;

    char[] trc = { ' ', '\n' };

    double RoundTo3(double d)
    {
        return (double)Math.round(d * 1000) / 1000;
    }

    void InitVariables()
    {
        //tmp1 = rtbXI.Text.Trim(trc).Split('\n');
        //tmp2 = rtbFI.Text.Trim(trc).Split('\n');

        String t1 = String.valueOf(((EditText)findViewById(R.id.etXi)).getText());
        tmp1 = t1.trim().split("\n");
        String t2 = String.valueOf(((EditText)findViewById(R.id.etFi)).getText());
        tmp2 = t2.trim().split("\n");

        Xi = new double[tmp1.length];
        Fi = new double[tmp2.length];
        XiFi = new double[tmp2.length];
        XiDev = new double[tmp2.length];
        FiDev = new double[tmp2.length];
        XiMDev = new double[tmp2.length];
        FiMDev = new double[tmp2.length];
        CF = new double[tmp2.length];
        sum = sum2 = sum3 = N = 0;
    }

    void SetXi()
    {
        for (int i = 0; i < tmp1.length; i++)
        {
            Xi[i] = Double.valueOf(tmp1[i]);
        }
    }

    void SetFi()
    {
        for (int i = 0; i < tmp2.length; i++)
        {
            Fi[i] = Double.valueOf(tmp2[i]);
        }
    }

    void SetCF()
    {
        CF[0] = Fi[0];

        for (int i = 1; i < tmp2.length; i++)
        {
            CF[i] = CF[i - 1] + Fi[i];
        }

        String tmps = "";
        for (int i = 0; i < CF.length; i++)
        {
            tmps += String.valueOf(CF[i]) + '\n';
        }
        //rtbCF.Text = tmps;
        ((EditText)findViewById(R.id.etCF)).setText(tmps);
    }

    void SetXiFi()
    {
        for (int i = 0; i < Xi.length; i++)
        {
            XiFi[i] = Xi[i] * Fi[i];
        }

        String tmps = "";
        for (int i = 0; i < XiFi.length; i++)
        {
            tmps += String.valueOf(RoundTo3(XiFi[i])) + '\n';
        }
        //rtbFIXI.Text = tmps;
        ((EditText)findViewById(R.id.etFiXi)).setText(tmps);
    }

    void SetXiFiSum()
    {
        for (int i = 0; i < Xi.length; i++)
        {
            sum += XiFi[i];
        }
    }

    void SetN()
    {
        for (int i = 0; i < Fi.length; i++)
        {
            N += Fi[i];
        }
    }

    void SetMean()
    {
        mean = sum / N;

        //tbMean.Text = Math.round(mean).ToString();
        ((EditText)findViewById(R.id.etMean)).setText(String.valueOf(RoundTo3(mean)));
    }

    void SetXiDev()
    {
        for (int i = 0; i < Xi.length; i++)
        {
            XiDev[i] = Math.abs(Xi[i] - mean);
        }

        String tmps = "";
        for (int i = 0; i < XiFi.length; i++)
        {
            tmps += String.valueOf(RoundTo3(XiDev[i])) + '\n';
        }
        //rtbDEV.Text = tmps;???
        ((EditText)findViewById(R.id.etXiM)).setText(tmps);
    }

    void SetFiDev()
    {
        for (int i = 0; i < XiDev.length; i++)
        {
            FiDev[i] = Fi[i] * XiDev[i];
        }

        String tmps = "";
        for (int i = 0; i < FiDev.length; i++)
        {
            tmps += String.valueOf(RoundTo3(FiDev[i])) + '\n';
        }
        //rtbFIDEV.Text = tmps;???
        ((EditText)findViewById(R.id.etFiXiM)).setText(tmps);
    }

    void SetFiDevSum()
    {
        for (int i = 0; i < FiDev.length; i++)
        {
            sum2 += FiDev[i];
        }
    }

    void SetMeanDeviationAboutMean()
    {
        mdm = sum2 / N;

        //tbMDM.Text = String.valueOf(Math.round(mdm));

        ((EditText)findViewById(R.id.etDevM)).setText(String.valueOf(RoundTo3(mdm)));
    }

    void SetXiMedianDeviation()
    {
        for (int i = 0; i < Xi.length; i++)
        {
            XiMDev[i] = Math.abs(Xi[i] - median);
        }

        String tmps = "";
        for (int i = 0; i < XiMDev.length; i++)
        {
            tmps += String.valueOf(RoundTo3(XiMDev[i])) + '\n';
        }
        //rtbDevMedian.Text = tmps;

        ((EditText)findViewById(R.id.etXiMd)).setText(tmps);
    }

    void SetFiMedianDeviation()
    {
        for (int i = 0; i < FiMDev.length; i++)
        {
            FiMDev[i] = Fi[i] * XiMDev[i];
        }

        String tmps = "";
        for (int i = 0; i < FiMDev.length; i++)
        {
            tmps += String.valueOf(RoundTo3(FiMDev[i])) + '\n';
        }

        //rtbFIDevMedian.Text = tmps;???
        ((EditText)findViewById(R.id.etFiXiMd)).setText(tmps);
    }

    void SetFiMDevSum()
    {
        for (int i = 0; i < FiMDev.length; i++)
        {
            sum3 += FiMDev[i];
        }
    }

    void SetMeanDeviationAboutMedian()
    {
        mdmedian = sum3 / N;

        //tbMDMedian.Text = Math.Round(mdmedian, 3).ToString();

        ((EditText)findViewById(R.id.etDevMd)).setText(String.valueOf(RoundTo3(mdmedian)));
    }

    void CalculateAndPrint()
    {
        InitVariables();

        SetXi();

        SetFi();

        SetCF();

        SetXiFi();

        SetXiFiSum();

        SetN();

        SetMean();

        SetXiDev();

        SetFiDev();

        SetFiDevSum();

        SetMeanDeviationAboutMean();

        CalculateMedian();

        SetXiMedianDeviation();

        SetFiMedianDeviation();

        SetFiMDevSum();

        SetMeanDeviationAboutMedian();
    }

    void CalculateMedian()
    {
        if (N % 2 != 0)
            even = false;
        else
            even = true;

        if (even)
        {
            int nb2 = (int)(N / 2);
            int nb2p1 = nb2 + 1;
            double nb2t = 0, nb2p1t = 0;

            for (int i = 0; i < CF.length; i++)
            {
                if ((int)CF[i] == nb2)
                {
                    nb2t = Xi[i];
                    break;
                }

                if (i > 0)
                {
                    if (nb2 < CF[i] && nb2 > CF[i - 1])
                    {
                        nb2t = Xi[i];
                        break;
                    }
                }
            }

            for (int i = 0; i < CF.length; i++)
            {
                if ((int)CF[i] == nb2p1)
                {
                    nb2p1t = Xi[i];
                    break;
                }

                if (i > 0)
                {
                    if (nb2p1 < CF[i] && nb2p1 > CF[i - 1])
                    {
                        nb2p1t = Xi[i];
                        break;
                    }
                }
            }

            median = (nb2t + nb2p1t) / 2;
        }
        else
        {
            int cfv = 0, cfiv = 0;
            double tmp1s = (N + 1) / 2;
            int tmp1 = (int)tmp1s;

            for (int i = 0; i < CF.length; i++)
            {
                if ((int)CF[i] == tmp1)
                {
                    cfv = (int)CF[i];
                    cfiv = i;
                    break;
                }

                if (i > 0)
                {
                    if (tmp1s < CF[i] && tmp1 > CF[i - 1])
                    {
                        cfv = (int)CF[i];
                        cfiv = i;
                        break;
                    }
                }
            }

            median = Xi[cfiv];
        }

        ((EditText)findViewById(R.id.etMd)).setText(String.valueOf(median));
    }

}
