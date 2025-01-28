package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.IMU;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp
public class fieldcentricRyo extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        // Declare our motors
        // Make sure your ID's match your configuration
        DcMotor fl = hardwareMap.dcMotor.get("fl");
        DcMotor bl = hardwareMap.dcMotor.get("bl");
        DcMotor fr = hardwareMap.dcMotor.get("fr");
        DcMotor br = hardwareMap.dcMotor.get("br");
        DcMotor leftHang = hardwareMap.dcMotor.get("leftHang");
        DcMotor rightHang = hardwareMap.dcMotor.get("rightHang");
        DcMotor leftLift = hardwareMap.dcMotor.get("LL");
        DcMotor rightLift = hardwareMap.dcMotor.get("RL");
        Servo claw = hardwareMap.servo.get("claw");
        Servo leftWrist = hardwareMap.servo.get("leftWrist");
        Servo rightWrist = hardwareMap.servo.get("rightWrist");

        leftLift.setDirection(DcMotorSimple.Direction.FORWARD);
        leftLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        rightLift.setDirection(DcMotorSimple.Direction.FORWARD);
        rightLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // See the note about this earlier on this page.
        fr.setDirection(DcMotorSimple.Direction.FORWARD); //the only one thats not reversed T-T
        br.setDirection(DcMotorSimple.Direction.REVERSE);

        // Retrieve the IMU from the hardware map
        IMU imu = hardwareMap.get(IMU.class, "imu");
        // Adjust the orientation parameters to match your robot
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.FORWARD,
                RevHubOrientationOnRobot.UsbFacingDirection.UP));
        // Without this, the REV Hub's orientation is assumed to be logo up / USB forward
        imu.initialize(parameters);

        waitForStart();

        if (isStopRequested()) return;

        while (opModeIsActive()) {
            double y = -gamepad1.left_stick_y; // Remember, Y stick value is reversed
            double x = gamepad1.left_stick_x;
            double rx = gamepad1.right_stick_x;

            // This button choice was made so that it is hard to hit on accident,
            // it can be freely changed based on preference.
            // The equivalent button is start on Xbox-style controllers.
            if (gamepad1.options) {
                imu.resetYaw();
            }

            double botHeading = imu.getRobotYawPitchRollAngles().getYaw(AngleUnit.RADIANS);

            // Rotate the movement direction counter to the bot's rotation
            double rotX = x * Math.cos(-botHeading) - y * Math.sin(-botHeading);
            double rotY = x * Math.sin(-botHeading) + y * Math.cos(-botHeading);

            rotX = rotX * 1.1;  // Counteract imperfect strafing

            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio,
            // but only if at least one is out of the range [-1, 1]
            double denominator = Math.max(Math.abs(rotY) + Math.abs(rotX) + Math.abs(rx), 1);
            double frontLeftPower = (rotY + rotX + rx) / denominator;
            double backLeftPower = (rotY - rotX + rx) / denominator;
            double frontRightPower = (rotY - rotX - rx) / denominator;
            double backRightPower = (rotY + rotX - rx) / denominator;

            fl.setPower(frontLeftPower);
            bl.setPower(backLeftPower);
            fr.setPower(frontRightPower);
            br.setPower(backRightPower);

            //Added TeleOp into the field centric if it doesn't work we will go back to og
            //Front back Left
            if (Math.abs(gamepad1.left_stick_y) > .2) {
                fl.setPower(gamepad1.left_stick_y * 1);
                bl.setPower(gamepad1.left_stick_y * -1);
            } else {
                fl.setPower(0);
                bl.setPower(0);
            }

            //Front back Right
            if (Math.abs(gamepad1.right_stick_y) > .2) {
                fr.setPower(gamepad1.right_stick_y * -1);
                br.setPower(gamepad1.right_stick_y * 1);
            } else {
                fr.setPower(0);
                br.setPower(0);
            }

            //Side speed Right
            if (gamepad1.right_bumper) {
                fl.setPower(1);
                bl.setPower(1);
                fr.setPower(1);
                br.setPower(1);
            } else {
                fl.setPower(0);
                bl.setPower(0);
                fr.setPower(0);
                br.setPower(0);
            }

            //Side speed Left
            if (gamepad1.left_bumper) {
                fl.setPower(-1);
                bl.setPower(-1);
                fr.setPower(-1);
                br.setPower(-1);
            } else {
                fl.setPower(0);
                bl.setPower(0);
                fr.setPower(0);
                br.setPower(0);
            }
            //up and down p2
            //Lift
            // Needs to be faster down AND slower up
            if (gamepad2.left_bumper) {
                rightLift.setPower(-.5);
                leftLift.setPower(-.5);

            } else if (gamepad2.right_bumper) {
                rightLift.setPower(0.9);
                leftLift.setPower(0.9);
            } else {
                rightLift.setPower(0);
                leftLift.setPower(0);
            }

            // Intake out
            if (gamepad2.b) { //close all
                leftWrist.setPosition(0); // always goes up in values
                rightWrist.setPosition(0); // always goes up in values

            } else if (gamepad2.a) { //open all
                leftWrist.setPosition(.30);   // sigma sigma on the wall whos the skibidiest of them all
                rightWrist.setPosition(.30);

            } else if (gamepad2.x) {
                leftWrist.setPosition(.60);
                rightWrist.setPosition(.60);

            }
            // Hang to go up and down
            if (gamepad2.dpad_up) {
                leftHang.setPower(.9);
                rightHang.setPower(.9);

            } else if (gamepad2.dpad_down) {
                leftHang.setPower(-.9);
                rightHang.setPower(-.9);

            } else {
                leftHang.setPower(0);
                rightHang.setPower(0);
            }
            //This is the wrist have to tweak it
            if (gamepad2.dpad_right) {
                claw.setPosition(1);

            } else if (gamepad2.dpad_left) {
                claw.setPosition(0.20);
            }
        }
    }
}