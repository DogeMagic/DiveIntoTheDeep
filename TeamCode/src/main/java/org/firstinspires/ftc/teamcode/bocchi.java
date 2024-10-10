package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "bocchi")
public class bocchi extends OpMode {
    IMU imu;
    DcMotor fl;
    DcMotor bl;
    DcMotor fr;
    DcMotor br;
    Servo Claw;
    DcMotor lift;


    @Override
    public void init() {
        fl = hardwareMap.dcMotor.get("fl");
        bl = hardwareMap.dcMotor.get("bl");
        fr = hardwareMap.dcMotor.get("fr");
        br = hardwareMap.dcMotor.get("br");
        imu = hardwareMap.get(IMU.class, "IMU");
        lift = hardwareMap.dcMotor.get("lift");
        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);


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
            fl.setPower(-1);
            bl.setPower(1);
            fr.setPower(-1);
            br.setPower(1);
        } else {
            fl.setPower(0);
            bl.setPower(0);
            fr.setPower(0);
            br.setPower(0);
        }

        //Side speed Left
        if (gamepad1.left_bumper) {
            fl.setPower(1);
            bl.setPower(-1);
            fr.setPower(1);
            br.setPower(-1);
        } else {
            fl.setPower(0);
            bl.setPower(0);
            fr.setPower(0);
            br.setPower(0);
        }
        //up and down p2
        //Lift
        if (gamepad2.left_bumper) {
            lift.setPower(-.9);
        } else if (gamepad2.right_bumper) {
            lift.setPower(.9);
        } else {
            lift.setPower(0);
        }
        // Intake out
        if (gamepad2.dpad_left) {
            Claw.setPosition(0);

        }
        else if (gamepad2.b){ //close all
            Claw.setPosition(.1);
        }
        else if (gamepad2.a){ //open all
            Claw.setPosition(.2);
        }
        else if (gamepad2.x) {
            Claw.setPosition(.2);

        } else if (gamepad2.y) {
            Claw.setPosition(.8);

        }
        }
    }