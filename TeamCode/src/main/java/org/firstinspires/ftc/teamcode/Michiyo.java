
package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

public class Michiyo {
    DcMotor frontLeft;
    DcMotor backLeft;
    DcMotor frontRight;
    DcMotor backRight;
    Servo leftClaw;
    Servo rightClaw;
    public Telemetry telem;

    boolean clawClose;

    private final ElapsedTime runtime = new ElapsedTime();

    static final double Counts_PER_MOTOR_REV = 1440;
    static final double DRIVE_GEAR_REDUCTION = 1.0;
    static final double WHEEL_DIAMETER_INCHES = 4.0;
    static final double COUNTS_PER_INCH = (Counts_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);

    static final double DRIVE_SPEED = 0.4;
    static final double TURN_SPEED = 0.3;

    public void init(HardwareMap hwmap, Telemetry telem) {
        frontLeft = hardwareMap.dcMotor.get("fL");
        backLeft = hardwareMap.dcMotor.get("bL");
        frontRight = hardwareMap.dcMotor.get("fR");
        backRight = hardwareMap.dcMotor.get("bR");
        leftClaw = hardwareMap.servo.get("LS");
        rightClaw = hardwareMap.servo.get("RS");


        frontLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeft.setDirection(DcMotorSimple.Direction.REVERSE);

        this.telem = telem;
    }

    public void encoderDrive(double speed, double leftInches, double rightInches, double timeouts) {
        int newLeftFrontTarget;
        int newRightFrontTarget;
        int newLeftBackTarget;
        int newRightBackTarget;

        newLeftFrontTarget =frontLeft.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
        newRightFrontTarget = frontRight.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
        newLeftBackTarget = backLeft.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
        newRightBackTarget = backRight.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
        frontLeft.setTargetPosition(newLeftFrontTarget);
        frontRight.setTargetPosition(newRightFrontTarget);
        backLeft.setTargetPosition(newLeftBackTarget);
        backRight.setTargetPosition(newRightBackTarget);

        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        runtime.reset();
        frontLeft.setPower(speed);
        frontRight.setPower(speed);
        backLeft.setPower(speed);
        backRight.setPower(speed);


        while (runtime.seconds() < timeouts && (frontLeft.isBusy() && frontRight.isBusy() && backLeft.isBusy() && backRight.isBusy())) {

            //telemetry.addData("Pat1", "Running to %7d :%7d", newLeftFrontTarget, newRightFrontTarget);
            //telemetry.addData("Path2", "Running to %7d :%7d", frontLeft.getCurrentPosition(), frontRight.getCurrentPosition());
            telem.addData("fL pos", frontLeft.getCurrentPosition());
            telem.addData("fR pos", frontRight.getCurrentPosition());
            telem.addData("bL pos", backLeft.getCurrentPosition());
            telem.addData("bR pos", backRight.getCurrentPosition());

            telem.addData("fL goal", newLeftFrontTarget);
            telem.addData("fR goal", newRightFrontTarget);
            telem.addData("bL goal", newLeftBackTarget);
            telem.addData("bR goal", newRightBackTarget);
            telem.update();
        }
        frontLeft.setPower(0);
        frontRight.setPower(0);
        backLeft.setPower(0);
        backRight.setPower(0);

        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void encoderLeftDrive(double speed, double rightInches, double timeouts) {
        //int newLeftFrontTarget;
        int newRightFrontTarget;
        //int newLeftBackTarget;
        int newRightBackTarget;

        //newLeftFrontTarget =frontLeft.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
        newRightFrontTarget = frontRight.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
        //  newLeftBackTarget = backLeft.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
        newRightBackTarget = backRight.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
        // frontLeft.setTargetPosition(newLeftFrontTarget);
        frontRight.setTargetPosition(newRightFrontTarget);
        //backLeft.setTargetPosition(newLeftBackTarget);
        backRight.setTargetPosition(newRightBackTarget);

        // frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        // backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        runtime.reset();
        // frontLeft.setPower(speed);
        frontRight.setPower(speed);
        //backLeft.setPower(speed);
        backRight.setPower(speed);


        while (runtime.seconds() < timeouts && (frontRight.isBusy() && backRight.isBusy())) {

            //telemetry.addData("Pat1", "Running to %7d :%7d", newLeftFrontTarget, newRightFrontTarget);
            //telemetry.addData("Path2", "Running to %7d :%7d", frontLeft.getCurrentPosition(), frontRight.getCurrentPosition());
            // telem.addData("fL pos", frontLeft.getCurrentPosition());
            telem.addData("fR pos", frontRight.getCurrentPosition());
            //  telem.addData("bL pos", backLeft.getCurrentPosition());
            telem.addData("bR pos", backRight.getCurrentPosition());

            //  telem.addData("fL goal", newLeftFrontTarget);
            telem.addData("fR goal", newRightFrontTarget);
            // telem.addData("bL goal", newLeftBackTarget);
            telem.addData("bR goal", newRightBackTarget);
            telem.update();
        }
        // frontLeft.setPower(0);
        frontRight.setPower(0);
        // backLeft.setPower(0);
        backRight.setPower(0);

        // frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        // backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void encoderRightDrive(double speed, double leftInches, double timeouts) {
        int newLeftFrontTarget;
        //  int newRightFrontTarget;
        int newLeftBackTarget;
        //   int newRightBackTarget;

        newLeftFrontTarget =frontLeft.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
        // newRightFrontTarget = frontRight.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
        newLeftBackTarget = backLeft.getCurrentPosition() + (int)(leftInches * COUNTS_PER_INCH);
        // newRightBackTarget = backRight.getCurrentPosition() + (int)(rightInches * COUNTS_PER_INCH);
        frontLeft.setTargetPosition(newLeftFrontTarget);
        //frontRight.setTargetPosition(newRightFrontTarget);
        backLeft.setTargetPosition(newLeftBackTarget);
        // backRight.setTargetPosition(newRightBackTarget);

        frontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        // frontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        backLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        //   backRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        runtime.reset();
        frontLeft.setPower(speed);
        // frontRight.setPower(speed);
        backLeft.setPower(speed);
        // backRight.setPower(speed);


        while (runtime.seconds() < timeouts && (frontLeft.isBusy() && backLeft.isBusy())) {

            //telemetry.addData("Pat1", "Running to %7d :%7d", newLeftFrontTarget, newRightFrontTarget);
            //telemetry.addData("Path2", "Running to %7d :%7d", frontLeft.getCurrentPosition(), frontRight.getCurrentPosition());
            telem.addData("fL pos", frontLeft.getCurrentPosition());
            // telem.addData("fR pos", frontRight.getCurrentPosition());
            telem.addData("bL pos", backLeft.getCurrentPosition());
            //telem.addData("bR pos", backRight.getCurrentPosition());

            telem.addData("fL goal", newLeftFrontTarget);
            // telem.addData("fR goal", newRightFrontTarget);
            telem.addData("bL goal", newLeftBackTarget);
            // telem.addData("bR goal", newRightBackTarget);
            telem.update();
        }
        frontLeft.setPower(0);
        // frontRight.setPower(0);
        backLeft.setPower(0);
        // backRight.setPower(0);

        frontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        // frontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        backLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        // backRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }


    public void StrafeLeft() {
        frontLeft.setPower(-.4);
        backLeft.setPower(.4);
        frontRight.setPower(.4);
        backRight.setPower(-.4);
    }
    public void StrafeRight() {
        frontLeft.setPower(-.4);
        backLeft.setPower(.4);
        frontRight.setPower(.4);
        backRight.setPower(-.4);
    }


    public void hold(){
        leftClaw.setPosition(0);
        rightClaw.setPosition(1.0);
        clawClose = true;
    }


    public void letGo(){
        leftClaw.setPosition(1);
        rightClaw.setPosition(0.6);
        clawClose = false;
    }
    public void letGoAll(){
        leftClaw.setPosition(1);
        rightClaw.setPosition(0);
        clawClose = false;
    }
    public void stayConnected(){
        frontLeft.setPower(.0);
        backLeft.setPower(.0);
        frontRight.setPower(.01);
        backRight.setPower(.01);
    }


}
