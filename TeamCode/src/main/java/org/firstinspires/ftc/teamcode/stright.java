package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@Autonomous(name = "stright")
public class stright extends LinearOpMode {

    DcMotor fl;
    DcMotor bl;
    DcMotor fr;
    DcMotor br;

    @Override
    public void runOpMode() throws InterruptedException {
        {
            fl=hardwareMap.dcMotor.get("fl");
            bl=hardwareMap.dcMotor.get("bl");
            fr=hardwareMap.dcMotor.get("fr");
            br=hardwareMap.dcMotor.get("br");

            fl.setDirection(DcMotorSimple.Direction.REVERSE);
            bl.setDirection(DcMotorSimple.Direction.FORWARD);
            fr.setDirection(DcMotorSimple.Direction.FORWARD);
            br.setDirection(DcMotorSimple.Direction.REVERSE);

            waitForStart();
            fl.setPower(0.35);
            bl.setPower(0.35);
            fr.setPower(0.35);
            br.setPower(0.35);
            sleep(1500);
        }
    }
}
