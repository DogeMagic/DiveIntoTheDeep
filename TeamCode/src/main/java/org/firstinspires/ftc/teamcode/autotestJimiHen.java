package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.firstinspires.ftc.robotcontroller.external.samples.SensorIMUNonOrthogonal;

@Autonomous(name = "autotestJimiHen")
public class autotestJimiHen extends LinearOpMode {

    DcMotor fl;
    DcMotor bl;
    DcMotor fr;
    DcMotor br;

    @Override
    public void runOpMode() throws InterruptedException {
        {
            fl = hardwareMap.dcMotor.get("fl");
            bl = hardwareMap.dcMotor.get("bl");
            fr = hardwareMap.dcMotor.get("fr");
            br = hardwareMap.dcMotor.get("br");
            waitForStart();
            //fl.setPower(-0.50);
            //bl.setPower(-0.50);
            //fr.setPower(0.50);
            //br.setPower(0.50);
            //sleep(1500);

            while (opModeIsActive()) {
                telemetry.addData("Robot Angle", getClass());
                telemetry.update();
            }

        }
    }
}
