package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@Autonomous(name = "autotestJimiHen")
public class autotestJimiHen extends LinearOpMode {

    DcMotor fl;
    DcMotor bl;
    DcMotor fr;
    DcMotor br;
    Servo rightclaw;
    Servo leftclaw;
    @Override
    public void runOpMode() throws InterruptedException {
        {
            fl=hardwareMap.dcMotor.get("fl");
            bl=hardwareMap.dcMotor.get("bl");
            fr=hardwareMap.dcMotor.get("fr");
            br=hardwareMap.dcMotor.get("br");
            waitForStart();
            fl.setPower(0.45);
            bl.setPower(-0.45);
            fr.setPower(0.45);
            br.setPower(-0.45);
            sleep(2500);
            fl.setPower(0.45);
            bl.setPower(0.45);
            fr.setPower(0.45);
            br.setPower(0.45);
            sleep(1000);
            fl.setPower(0.55);
            bl.setPower(0.55);
            fr.setPower(0.55);
            br.setPower(0.55);
            sleep(1500);
        }
    }
}
