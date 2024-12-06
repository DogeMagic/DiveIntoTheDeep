package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name = "temp")
public class temp extends LinearOpMode {

    DcMotor fr;
    DcMotor bl;
    DcMotor br;
    DcMotor fl;

    @Override
    public void runOpMode() throws InterruptedException {
        {
            fr=hardwareMap.dcMotor.get("fr");
            bl=hardwareMap.dcMotor.get("bl");
            br=hardwareMap.dcMotor.get("br");
            fl=hardwareMap.dcMotor.get("fl");
            waitForStart();
            fr.setPower(-0.35);
            bl.setPower(-0.35);
            fr.setPower(0.35);
            br.setPower(0.35);
            sleep(1500);
        }
    }
}