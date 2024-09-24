package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "TeleOp")
public class Teleop extends OpMode {

    DcMotor fl;
    DcMotor bl;
    DcMotor fr;
    DcMotor br;

    @Override
    public void init() {
        fl = hardwareMap.dcMotor.get("fL");
        bl = hardwareMap.dcMotor.get("bL");
        fr = hardwareMap.dcMotor.get("fR");
        br = hardwareMap.dcMotor.get("bR");

        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

    }

    @Override
    public void loop() {

        //Front back Left
        if (Math.abs(-gamepad1.left_stick_y) > .2) {
            fl.setPower(gamepad1.left_stick_y * 1);
            bl.setPower(gamepad1.left_stick_y * 1);
        } else {
            fl.setPower(0);
            bl.setPower(0);
        }

        //Front back Right
        if (Math.abs(gamepad1.right_stick_y) > .2) {
            fr.setPower(-gamepad1.right_stick_y * 1);
            br.setPower(-gamepad1.right_stick_y * 1);
        } else {
            fr.setPower(0);
            br.setPower(0);
        }

        //Side speed Right
        if (gamepad1.right_bumper) {
            fl.setPower(-.9);
            bl.setPower(.9);
            fr.setPower(-.9);
            br.setPower(.9);
        } else {
            fl.setPower(0);
            bl.setPower(0);
            fr.setPower(0);
            br.setPower(0);
        }
        //Side speed Left
        if (gamepad1.left_bumper) {
            fl.setPower(.9);
            bl.setPower(-.9);
            fr.setPower(.9);
            br.setPower(-.9);
        } else {
            fl.setPower(0);
            bl.setPower(0);
            fr.setPower(0);
            br.setPower(0);
        }
    }
}