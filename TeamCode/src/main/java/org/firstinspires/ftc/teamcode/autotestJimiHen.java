package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;

import static com.qualcomm.hardware.rev.RevHubOrientationOnRobot.xyzOrientation;

@Autonomous(name="autotestJimiHen", group="Robot")
public class autotestJimiHen extends LinearOpMode {

    /* Declare OpMode members. */
     DcMotor         bl   = null;
     DcMotor         br  = null;
     DcMotor         fr   = null;
     DcMotor         fl  = null;
     DcMotor         rightLift = null;
     DcMotor         leftLift = null;
     Servo           claw = null;
     Servo           wrist = null;
     ElapsedTime     runtime = new ElapsedTime();
     IMU             imu   = null;      // Control/Expansion Hub IMU

    double          headingError  = 0;

    // These variable are declared here (as class members) so they can be updated in various methods,
    // but still be displayed by sendTelemetry()
    double  targetHeading = 0;
    double  driveSpeed    = 0;
    double  turnSpeed     = 0;
    double  blSpeed     = 0;
    double  brSpeed    = 0;
    double  flSpeed     = 0;
    double  frSpeed    = 0;
    int     flTarget    = 0;
    int     blTarget    = 0;
    int     brTarget   = 0;
    int     frTarget   = 0;

    // Calculate the COUNTS_PER_INCH for your specific drive train.
    // Go to your motor vendor website to determine your motor's COUNTS_PER_MOTOR_REV
    // For external drive gearing, set DRIVE_GEAR_REDUCTION as needed.
    // For example, use a value of 2.0 for a 12-tooth spur gear driving a 24-tooth spur gear.
    // This is gearing DOWN for less speed and more torque.
    // For gearing UP, use a gear ratio less than 1.0. Note this will affect the direction of wheel rotation.
    static final double     COUNTS_PER_MOTOR_REV    = 537.7 ;   // eg: GoBILDA 312 RPM Yellow Jacket
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // No External Gearing.
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
            (WHEEL_DIAMETER_INCHES * 3.1415);

    // These constants define the desired driving/control characteristics
    // They can/should be tweaked to suit the specific robot drive train.
    static final double     DRIVE_SPEED             = 0.2
            ;     // Max driving speed for better distance accuracy.
    static final double     TURN_SPEED              = 0.2;     // Max turn speed to limit turn rate.
    static final double     HEADING_THRESHOLD       = 1.0 ;    // How close must the heading get to the target before moving to next step.
    // Requiring more accuracy (a smaller number) will often make the turn take longer to get into the final position.
    // Define the Proportional control coefficient (or GAIN) for "heading control".
    // We define one value when Turning (larger errors), and the other is used when Driving straight (smaller errors).
    // Increase these numbers if the heading does not correct strongly enough (eg: a heavy robot or using tracks)
    // Decrease these numbers if the heading does not settle on the correct value (eg: very agile robot with omni wheels)
    static final double     P_TURN_GAIN            = 0.02;     // Larger is more responsive, but also less stable.
    static final double     P_DRIVE_GAIN           = 0.02;     // Larger is more responsive, but also less stable.


    @Override
    public void runOpMode() {

        // Initialize the drive system variables.
        bl  = hardwareMap.get(DcMotor.class, "bl");
        br = hardwareMap.get(DcMotor.class, "br");
        fr  = hardwareMap.get(DcMotor.class, "fr");
        fl = hardwareMap.get(DcMotor.class, "fl");
        rightLift = hardwareMap.get(DcMotor.class, "RL");
        leftLift = hardwareMap.get(DcMotor.class, "LL");
        claw = hardwareMap.get(Servo.class, "claw");
        wrist = hardwareMap.get(Servo.class, "wrist");

        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // When run, this OpMode should start both motors driving forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips
        bl.setDirection(DcMotor.Direction.FORWARD);
        br.setDirection(DcMotor.Direction.REVERSE);
        fl.setDirection(DcMotor.Direction.REVERSE);
        fr.setDirection(DcMotor.Direction.FORWARD);

        rightLift.setDirection(DcMotorSimple.Direction.FORWARD);
        leftLift.setDirection(DcMotorSimple.Direction.FORWARD);

        /* The next two lines define Hub orientation.
         * The Default Orientation (shown) is when a hub is mounted horizontally with the printed logo pointing UP and the USB port pointing FORWARD.
         *
         * To Do:  EDIT these two lines to match YOUR mounting configuration.
         */
        RevHubOrientationOnRobot.LogoFacingDirection logoDirection = RevHubOrientationOnRobot.LogoFacingDirection.FORWARD;
        RevHubOrientationOnRobot.UsbFacingDirection  usbDirection  = RevHubOrientationOnRobot.UsbFacingDirection.UP;
        RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(logoDirection, usbDirection);

        // Now initialize the IMU with this mounting orientation
        // This sample expects the IMU to be in a REV Hub and named "imu".
        imu = hardwareMap.get(IMU.class, "imu");
        imu.initialize(new IMU.Parameters(orientationOnRobot));

        // Ensure the robot is stationary.  Reset the encoders and set the motors to BRAKE mode
        bl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        br.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        fr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);


        // Wait for the game to start (Display Gyro value while waiting)
        while (opModeInInit()) {
            telemetry.addData(">", "Robot Heading = %4.0f", getHeading());
            telemetry.update();
        }

        // Set the encoders for closed loop speed control, and reset the heading.
        bl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        br.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        fl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        fr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        imu.resetYaw();

        // Step through each leg of the path,
        // Notes:   Reverse movement is obtained by setting a negative distance (not speed)
        //          holdHeading() is used after turns to let the heading stabilize
        //          Add a sleep(2000) after any step to keep the telemetry data visible for review


        //Close the claw to grab the object
        moveServo(claw, 1, 1000);  // 1 = closed position

        strafe(0.5, 9.0, "left");  // Strafe left for 9 inches at 50% speed

        driveForward(0.5, .9);  // Drive forward .9 inches at 50% speed

        //Lift the object
        controllLift(2600, 0.8);  // Move lift to target position (2500 ticks)

        //Adjust the wrist for orientation
        moveServoGradually(wrist, 0.10, 0.38, 1000);  // Gradual wrist movement
        moveServo(claw, 0, 1000);  // 0 = open position

        telemetry.addData("Status", "eepy...");
        telemetry.update();
        sleep(3000);  // Pause for 2 seconds

        moveServoGradually(wrist, 0, 0, 1000);  // Gradual wrist movement

        //Lower the lift while keeping the claw closed
        controllLift(0, 0.8);  // Lower lift to bottom position

        // Open the claw to release the object
        moveServo(claw, 0, 1000);  // 0 = open position




        //turnToHeading( TURN_SPEED, -45.0);               // Turn  CW to -45 Degrees
       // holdHeading( TURN_SPEED, -45.0, 0.5);   // Hold -45 Deg heading for a 1/2 second

/*
        driveStraight(DRIVE_SPEED, 11.0, -45.0);  // Drive Forward 11" at -45 degrees (12"x and 12"y)
        turnToHeading( TURN_SPEED,  45.0);               // Turn  CCW  to  45 Degrees
        holdHeading( TURN_SPEED,  45.0, 0.5);    // Hold  45 Deg heading for a 1/2 second

        driveStraight(DRIVE_SPEED, 10.0, 45.0);  // Drive Forward 10" at 45 degrees (-12"x and 12"y)
        turnToHeading( TURN_SPEED,   0.0);               // Turn  CW  to 0 Degrees
        holdHeading( TURN_SPEED,   0.0, 1.0);    // Hold  0 Deg heading for 1 second

        driveStraight(DRIVE_SPEED,-9.0, 0.0);    // Drive in Reverse 9" (should return to approx. staring position)

        telemetry.addData("Path", "Complete");
        telemetry.update();
        sleep(1000);  // Pause to display last telemetry message.
        */
    }

    /*
     * ====================================================================================================
     * Driving "Helper" functions are below this line.
     * These provide the high and low level methods that handle driving straight and turning.
     * ====================================================================================================
     */

    // **********  HIGH Level driving functions.  ********************

    /**
     *  Drive in a straight line, on a fixed compass heading (angle), based on encoder counts.
     *  Move will stop if either of these conditions occur:
     *  1) Move gets to the desired position
     *  2) Driver stops the OpMode running.
     *
     * @param maxDriveSpeed MAX Speed for forward/rev motion (range 0 to +1.0) .
     * @param distance   Distance (in inches) to move from current position.  Negative distance means move backward.
     * @param heading      Absolute Heading Angle (in Degrees) relative to last gyro reset.
     *                   0 = fwd. +ve is CCW from fwd. -ve is CW from forward.
     *                   If a relative angle is required, add/subtract from the current robotHeading.
     */
    public void driveStraight(double maxDriveSpeed, double distance, double heading) {

        // Ensure that the OpMode is still active
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            int moveCounts = (int)(distance * COUNTS_PER_INCH);
            brTarget = br.getCurrentPosition() + moveCounts;
            blTarget = bl.getCurrentPosition() + moveCounts;
            frTarget = fr.getCurrentPosition() + moveCounts;
            flTarget = fl.getCurrentPosition() + moveCounts;

            // Set Target FIRST, then turn on RUN_TO_POSITION
            bl.setTargetPosition(blTarget);
            br.setTargetPosition(brTarget);
            fl.setTargetPosition(flTarget);
            fr.setTargetPosition(frTarget);

            bl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            br.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            fl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            fr.setMode(DcMotor.RunMode.RUN_TO_POSITION);


            // Set the required driving speed  (must be positive for RUN_TO_POSITION)
            // Start driving straight, and then enter the control loop
            maxDriveSpeed = Math.abs(maxDriveSpeed);
            moveRobot(maxDriveSpeed, 0);

            // keep looping while we are still active, and BOTH motors are running.
            while (opModeIsActive() &&
                    (bl.isBusy() && br.isBusy()) && fr.isBusy() && fl.isBusy()) {

                // Determine required steering to keep on heading
                turnSpeed = getSteeringCorrection(heading, P_DRIVE_GAIN);

                // if driving in reverse, the motor correction also needs to be reversed
                if (distance < 0)
                    turnSpeed *= -1.0;

                // Apply the turning correction to the current driving speed.
                moveRobot(driveSpeed, turnSpeed);

                // Display drive status for the driver.
                sendTelemetry(true);
            }

            // Stop all motion & Turn off RUN_TO_POSITION
            moveRobot(0, 0);
            br.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            bl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            fr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            fl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }

    /**
     *  Spin on the central axis to point in a new direction.
     *  <p>
     *  Move will stop if either of these conditions occur:
     *  <p>
     *  1) Move gets to the heading (angle)
     *  <p>
     *  2) Driver stops the OpMode running.
     *
     * @param maxTurnSpeed Desired MAX speed of turn. (range 0 to +1.0)
     * @param heading Absolute Heading Angle (in Degrees) relative to last gyro reset.
     *              0 = fwd. +ve is CCW from fwd. -ve is CW from forward.
     *              If a relative angle is required, add/subtract from current heading.
     */
    public void turnToHeading(double maxTurnSpeed, double heading) {

        // Run getSteeringCorrection() once to pre-calculate the current error
        getSteeringCorrection(heading, P_DRIVE_GAIN);

        // keep looping while we are still active, and not on heading.
        while (opModeIsActive() && (Math.abs(headingError) > HEADING_THRESHOLD)) {

            // Determine required steering to keep on heading
            turnSpeed = getSteeringCorrection(heading, P_TURN_GAIN);

            // Clip the speed to the maximum permitted value.
            turnSpeed = Range.clip(turnSpeed, -maxTurnSpeed, maxTurnSpeed);

            // Pivot in place by applying the turning correction
            moveRobot(0, turnSpeed);

            // Display drive status for the driver.
            sendTelemetry(false);
        }

        // Stop all motion;
        moveRobot(0, 0);
    }

    /**
     *  Obtain & hold a heading for a finite amount of time
     *  <p>
     *  Move will stop once the requested time has elapsed
     *  <p>
     *  This function is useful for giving the robot a moment to stabilize its heading between movements.
     *
     * @param maxTurnSpeed      Maximum differential turn speed (range 0 to +1.0)
     * @param heading    Absolute Heading Angle (in Degrees) relative to last gyro reset.
     *                   0 = fwd. +ve is CCW from fwd. -ve is CW from forward.
     *                   If a relative angle is required, add/subtract from current heading.
     * @param holdTime   Length of time (in seconds) to hold the specified heading.
     */
    public void holdHeading(double maxTurnSpeed, double heading, double holdTime) {

        ElapsedTime holdTimer = new ElapsedTime();
        holdTimer.reset();

        // keep looping while we have time remaining.
        while (opModeIsActive() && (holdTimer.time() < holdTime)) {
            // Determine required steering to keep on heading
            turnSpeed = getSteeringCorrection(heading, P_TURN_GAIN);

            // Clip the speed to the maximum permitted value.
            turnSpeed = Range.clip(turnSpeed, -maxTurnSpeed, maxTurnSpeed);

            // Pivot in place by applying the turning correction
            moveRobot(0, turnSpeed);

            // Display drive status for the driver.
            sendTelemetry(false);
        }

        // Stop all motion;
        moveRobot(0, 0);
    }

    // **********  LOW Level driving functions.  ********************

    /**
     * Use a Proportional Controller to determine how much steering correction is required.
     *
     * @param desiredHeading        The desired absolute heading (relative to last heading reset)
     * @param proportionalGain      Gain factor applied to heading error to obtain turning power.
     * @return                      Turning power needed to get to required heading.
     */
    public double getSteeringCorrection(double desiredHeading, double proportionalGain) {
        targetHeading = desiredHeading;  // Save for telemetry

        // Determine the heading current error
        headingError = targetHeading - getHeading();

        // Normalize the error to be within +/- 180 degrees
        while (headingError > 180)  headingError -= 360;
        while (headingError <= -180) headingError += 360;

        // Multiply the error by the gain to determine the required steering correction/  Limit the result to +/- 1.0
        return Range.clip(headingError * proportionalGain, -1, 1);
    }

    /**
     * Take separate drive (fwd/rev) and turn (right/left) requests,
     * combines them, and applies the appropriate speed commands to the left and right wheel motors.
     * @param drive forward motor speed
     * @param turn  clockwise turning motor speed.
     */
    public void moveRobot(double drive, double turn) {
        driveSpeed = drive;     // save this value as a class member so it can be used by telemetry.
        turnSpeed  = turn;      // save this value as a class member so it can be used by telemetry.

        blSpeed  = drive - turn;
        brSpeed = drive + turn;
        flSpeed  = drive - turn;
        frSpeed = drive + turn;


        // Scale speeds down if either one exceeds +/- 1.0;
        double max = Math.max(Math.abs(blSpeed), Math.abs(brSpeed));
        if (max > 1.0)
        {
            blSpeed /= max;
            brSpeed /= max;
            flSpeed /= max;
            frSpeed /= max;
        }

        bl.setPower(blSpeed);
        br.setPower(brSpeed);
        fl.setPower(flSpeed);
        fr.setPower(frSpeed);
    }

    /**
     *  Display the various control parameters while driving
     *
     * @param straight  Set to true if we are driving straight, and the encoder positions should be included in the telemetry.
     */
    private void sendTelemetry(boolean straight) {

        if (straight) {
            telemetry.addData("Motion", "Drive Straight");
            telemetry.addData("Target Pos L:R",  "%7d:%7d",       blTarget,  brTarget, flTarget, frTarget);
            telemetry.addData("Actual Pos L:R",  "%7d:%7d",      bl.getCurrentPosition(),
                    br.getCurrentPosition(), fl.getCurrentPosition(), fr.getCurrentPosition());
        } else {
            telemetry.addData("Motion", "Turning");
        }

        telemetry.addData("Heading- Target : Current", "%5.2f : %5.0f", targetHeading, getHeading());
        telemetry.addData("Error  : Steer Pwr",  "%5.1f : %5.1f", headingError, turnSpeed);
        telemetry.addData("Wheel Speeds L : R", "%5.2f : %5.2f", blSpeed, brSpeed,flSpeed, frSpeed);
        telemetry.addData("BR Distance: ", br.getCurrentPosition());
        telemetry.addData("BL Distance: ", bl.getCurrentPosition());
        telemetry.addData("FR Distance: ", fr.getCurrentPosition());
        telemetry.addData("FL Distance: ", fl.getCurrentPosition());
        telemetry.update();
    }
    public void moveServo(Servo servo, double position, long waitTime) {
        // Set the servo to the position
        servo.setPosition(position);

        // Optional: Wait for the servo to move
        if (waitTime > 0) {
            sleep(waitTime);
        }
    }
    public void moveServoGradually(Servo servo, double start, double end, long duration) {
        int steps = 20;  // Number of steps for smooth movement
        double stepSize = (end - start) / steps;
        long stepTime = duration / steps;

        for (int i = 0; i <= steps; i++) {
            double currentPosition = start + (i * stepSize);
            servo.setPosition(currentPosition);
            sleep(stepTime);
        }
    }

    /**
     * read the Robot heading directly from the IMU (in degrees)
     */
    public double getHeading() {
        YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
        return orientation.getYaw(AngleUnit.DEGREES);
    }
    public void driveForward(double speed, double distance) {
        int newFrontLeftTarget, newFrontRightTarget, newBackLeftTarget, newBackRightTarget;

        if (opModeIsActive()) {
            // Calculate the target position in encoder ticks
            int moveCounts = (int) (distance * COUNTS_PER_INCH); // Replace COUNTS_PER_INCH with your specific value

            // Determine the new target position for each motor
            newFrontLeftTarget = fl.getCurrentPosition() + moveCounts;
            newFrontRightTarget = fr.getCurrentPosition() + moveCounts;
            newBackLeftTarget = bl.getCurrentPosition() + moveCounts;
            newBackRightTarget = br.getCurrentPosition() + moveCounts;

            // Set target positions for each motor
            fl.setTargetPosition(newFrontLeftTarget);
            fr.setTargetPosition(newFrontRightTarget);
            bl.setTargetPosition(newBackLeftTarget);
            br.setTargetPosition(newBackRightTarget);

            // Set all motors to RUN_TO_POSITION
            fl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            fr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            bl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            br.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // Start moving the motors
            fl.setPower(Math.abs(speed));
            fr.setPower(Math.abs(speed));
            bl.setPower(Math.abs(speed));
            br.setPower(Math.abs(speed));

            // Keep looping while all motors are busy and the OpMode is active
            while (opModeIsActive() &&
                    (fl.isBusy() && fr.isBusy() && bl.isBusy() && br.isBusy())) {
                // Optional: Add telemetry to display progress
                telemetry.addData("Path", "Driving Forward");
                telemetry.addData("Front Left", fl.getCurrentPosition());
                telemetry.addData("Front Right", fr.getCurrentPosition());
                telemetry.addData("Back Left", bl.getCurrentPosition());
                telemetry.addData("Back Right", br.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion
            fl.setPower(0);
            fr.setPower(0);
            bl.setPower(0);
            br.setPower(0);

            // Reset all motors to RUN_USING_ENCODER
            fl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            fr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            bl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            br.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }
    public void strafe(double speed, double distance, String direction) {
        int newFrontLeftTarget, newFrontRightTarget, newBackLeftTarget, newBackRightTarget;

        if (opModeIsActive()) {
            // Calculate the target position in encoder ticks
            int moveCounts = (int) (distance * COUNTS_PER_INCH); // Replace COUNTS_PER_INCH with your specific value

            // Determine the direction and set target positions
            if (direction.equalsIgnoreCase("left")) {
                newFrontLeftTarget = fl.getCurrentPosition() - moveCounts;
                newFrontRightTarget = fr.getCurrentPosition() + moveCounts;
                newBackLeftTarget = bl.getCurrentPosition() + moveCounts;
                newBackRightTarget = br.getCurrentPosition() - moveCounts;
            } else if (direction.equalsIgnoreCase("right")) {
                newFrontLeftTarget = fl.getCurrentPosition() + moveCounts;
                newFrontRightTarget = fr.getCurrentPosition() - moveCounts;
                newBackLeftTarget = bl.getCurrentPosition() - moveCounts;
                newBackRightTarget = br.getCurrentPosition() + moveCounts;
            } else {
                telemetry.addData("Error", "Invalid direction specified: %s", direction);
                telemetry.update();
                return;
            }

            // Set target positions for each motor
            fl.setTargetPosition(newFrontLeftTarget);
            fr.setTargetPosition(newFrontRightTarget);
            bl.setTargetPosition(newBackLeftTarget);
            br.setTargetPosition(newBackRightTarget);

            // Set all motors to RUN_TO_POSITION
            fl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            fr.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            bl.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            br.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // Start moving the motors
            fl.setPower(Math.abs(speed));
            fr.setPower(Math.abs(speed));
            bl.setPower(Math.abs(speed));
            br.setPower(Math.abs(speed));

            // Keep looping while all motors are busy and the OpMode is active
            while (opModeIsActive() &&
                    (fl.isBusy() && fr.isBusy() && bl.isBusy() && br.isBusy())) {
                // Optional: Add telemetry to display progress
                telemetry.addData("Path", "Strafing %s", direction);
                telemetry.addData("Front Left", fl.getCurrentPosition());
                telemetry.addData("Front Right", fr.getCurrentPosition());
                telemetry.addData("Back Left", bl.getCurrentPosition());
                telemetry.addData("Back Right", br.getCurrentPosition());
                telemetry.update();
            }

            // Stop all motion
            fl.setPower(0);
            fr.setPower(0);
            bl.setPower(0);
            br.setPower(0);

            // Reset all motors to RUN_USING_ENCODER
            fl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            fr.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            bl.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            br.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
    }
    public void controllLift (int targetPosition, double speed) {

        // Set target position for both lifts
        if (opModeIsActive()) {

            // Determine new target position, and pass to motor controller
            leftLift.setTargetPosition(targetPosition);
            rightLift.setTargetPosition(targetPosition);

            // Turn On RUN_TO_POSITION
            leftLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            rightLift.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            // reset the timeout time and start motion.
            runtime.reset();
            leftLift.setPower(Math.abs(speed));
            rightLift.setPower(Math.abs(speed));

            // Keep looping while we are still active, and both motors are running
            while (opModeIsActive() && (leftLift.isBusy() && rightLift.isBusy())) {
                telemetry.addData("Left Lift Position", leftLift.getCurrentPosition());
                telemetry.addData("Right Lift Position", rightLift.getCurrentPosition());
                telemetry.update();
            }

            // Stop the motors
            leftLift.setPower(0);
            rightLift.setPower(0);

            // Reset motor modes to RUN_USING_ENCODER
            leftLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            rightLift.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        }
}
}
