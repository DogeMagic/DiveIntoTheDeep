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

        // Reverse the right side motors. This may be wrong for your setup.
        // If your robot moves backwards when commanded to go forwards,
        // reverse the left side instead.
        // See the note about this earlier on this page.
        fr.setDirection(DcMotorSimple.Direction.REVERSE);
        br.setDirection(DcMotorSimple.Direction.REVERSE);

        // Retrieve the IMU from the hardware map
        IMU imu = hardwareMap.get(IMU.class, "imu");
        // Adjust the orientation parameters to match your robot
        IMU.Parameters parameters = new IMU.Parameters(new RevHubOrientationOnRobot(
                RevHubOrientationOnRobot.LogoFacingDirection.UP,
                RevHubOrientationOnRobot.UsbFacingDirection.FORWARD));
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
            @TeleOp(name = "bocchi")
            class bocchi extends OpMode {
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
                    leftClaw = hardwareMap.servo.get("LC");
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
                        fl.setPower(-1);
                        bl.setPower(1);
                        fr.setPower(1);
                        br.setPower(-1);
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
                        fr.setPower(-1);
                        br.setPower(1);
                    } else {
                        fl.setPower(0);
                        bl.setPower(0);
                        fr.setPower(0);
                        br.setPower(0);
                    }
                    //up and down p2
                    //Lift
                    if (gamepad2.left_bumper) {
                        leftLift.setPower(-.6);
                        rightLift.setPower(.6);

                    } else if (gamepad2.right_bumper) {
                        leftLift.setPower(.2);
                        rightLift.setPower(-.2);

                    } else {
                        leftLift.setPower(0);
                        rightLift.setPower(0);
                    }
                    // Intake out
                    if (gamepad2.b) { //close all
                        leftClaw.setPosition(0);
                        rightClaw.setPosition(1);
                    } else if (gamepad2.a) { //open all
                        rightClaw.setPosition(.5);
                        leftClaw.setPosition(.45);
                    } else if (gamepad2.x) { //open left
                        leftClaw.setPosition(.10);

                    } else if (gamepad2.y) { //opens right
                        rightClaw.setPosition(.10);

                    }
                }
            }
        }
    }
}
