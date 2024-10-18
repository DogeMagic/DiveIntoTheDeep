package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp(name = "bocchi")
public class bocchi extends OpMode {
    DcMotor fl;
    DcMotor bl;
    DcMotor fr;
    DcMotor br;
    DcMotor leftLift;
    DcMotor rightLift;
    Servo rightClaw;
    Servo leftClaw;


    @Override
    public void init() {
        fl = hardwareMap.dcMotor.get("fl");
        bl = hardwareMap.dcMotor.get("bl");
        fr = hardwareMap.dcMotor.get("fr");
        br = hardwareMap.dcMotor.get("br");
        leftClaw =hardwareMap.servo.get("LC");
        rightClaw = hardwareMap.servo.get("RC");
        leftLift = hardwareMap.dcMotor.get("LL");
        rightLift = hardwareMap.dcMotor.get("RL");
        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        leftLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        rightLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);


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
            fl.setPower(-.7);
            bl.setPower(.7);
            fr.setPower(.7);
            br.setPower(-.7);
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
            leftLift.setPower(-.9);
            rightLift.setPower(-.9);
            
        } else if (gamepad2.right_bumper) {
            leftLift.setPower(.9);
            rightLift.setPower(.9);
            
        } else {
            leftLift.setPower(0);
            rightLift.setPower(0);
        }
        // Intake out
        if (gamepad2.b){ //close all
            leftClaw.setPosition(0);
            rightClaw.setPosition(1);
        }
        else if (gamepad2.a){ //open all
            rightClaw.setPosition(.5);
            leftClaw.setPosition(.45);
        }
        else if (gamepad2.x) { //open left
            leftClaw.setPosition(.10);

        } else if (gamepad2.y) { //opens right
            rightClaw.setPosition(.10);

        }
    }
}
