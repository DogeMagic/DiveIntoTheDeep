package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.IMU;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AngularVelocity;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.YawPitchRollAngles;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.util.ElapsedTime;

import static com.qualcomm.hardware.rev.RevHubOrientationOnRobot.xyzOrientation;


@TeleOp(name = "autotestJimiHen", group = "Sensor")
@Disabled     // Comment this out to add to the OpMode list
public class autotestJimiHen extends LinearOpMode {
    private IMU             imu         = null;      // Control/Expansion Hub IMU
    private DcMotor         fr   = null;
    private DcMotor         fl  = null;
    private DcMotor         bl  = null;
    private DcMotor         br  = null;


    private ElapsedTime runtime = new ElapsedTime();

    static final double     COUNTS_PER_MOTOR_REV    = 1440 ;    // eg: TETRIX Motor Encoder
    static final double     DRIVE_GEAR_REDUCTION    = 1.0 ;     // No External Gearing.
    static final double     WHEEL_DIAMETER_INCHES   = 4.0 ;     // For figuring circumference
    static final double     COUNTS_PER_INCH         = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) /
                                                                (WHEEL_DIAMETER_INCHES * 3.1415);
    static final double     DRIVE_SPEED             = 0.6;
    static final double     TURN_SPEED              = 0.5;
    static final double     HEADING_THRESHOLD       = 1.0 ;
    static final double     P_TURN_GAIN            = 0.02;
    static final double     P_DRIVE_GAIN           = 0.03;

    @Override
    public void runOpMode() throws InterruptedException {
        {
            fl = hardwareMap.dcMotor.get("fl");
            bl = hardwareMap.dcMotor.get("bl");
            fr = hardwareMap.dcMotor.get("fr");
            br = hardwareMap.dcMotor.get("br");
            imu = hardwareMap.get(IMU.class, "imu");

           /* private double  targetHeading = 0;
            private double  driveSpeed    = 0;
            private double  turnSpeed     = 0;
            private double  leftSpeed     = 0;
            private double  rightSpeed    = 0;
            private int     leftTarget    = 0;
            private int     rightTarget   = 0;
            double          headingError  = 0;

            */

            double xRotation = 0;  // enter the desired X rotation angle here.
            double yRotation = 0;  // enter the desired Y rotation angle here.
            double zRotation = 0;  // enter the desired Z rotation angle here.

            Orientation hubRotation = xyzOrientation(xRotation, yRotation, zRotation);

            // Now initialize the IMU with this mounting orientation
            RevHubOrientationOnRobot orientationOnRobot = new RevHubOrientationOnRobot(hubRotation);
            imu.initialize(new IMU.Parameters(orientationOnRobot));
            // Loop and update the dashboard
            while (!isStopRequested()) {
                telemetry.addData("Hub orientation", "X=%.1f,  Y=%.1f,  Z=%.1f \n", xRotation, yRotation, zRotation);

                // Check to see if heading reset is requested
                if (gamepad1.y) {
                    telemetry.addData("Yaw", "Resetting\n");
                    imu.resetYaw();
                } else {
                    telemetry.addData("Yaw", "Press Y (triangle) on Gamepad to reset\n");
                }
                // Retrieve Rotational Angles and Velocities
                YawPitchRollAngles orientation = imu.getRobotYawPitchRollAngles();
                AngularVelocity angularVelocity = imu.getRobotAngularVelocity(AngleUnit.DEGREES);

                telemetry.addData("Yaw (Z)", "%.2f Deg. (Heading)", orientation.getYaw(AngleUnit.DEGREES));
                telemetry.addData("Pitch (X)", "%.2f Deg.", orientation.getPitch(AngleUnit.DEGREES));
                telemetry.addData("Roll (Y)", "%.2f Deg.\n", orientation.getRoll(AngleUnit.DEGREES));
                telemetry.addData("Yaw (Z) velocity", "%.2f Deg/Sec", angularVelocity.zRotationRate);
                telemetry.addData("Pitch (X) velocity", "%.2f Deg/Sec", angularVelocity.xRotationRate);
                telemetry.addData("Roll (Y) velocity", "%.2f Deg/Sec", angularVelocity.yRotationRate);
                telemetry.update();


               /* driveStraight(DRIVE_SPEED, 24.0, 0.0);    // Drive Forward 24"
                turnToHeading(TURN_SPEED, -45.0);               // Turn  CW to -45 Degrees
                holdHeading(TURN_SPEED, -45.0, 0.5);   // Hold -45 Deg heading for a 1/2 second

                driveStraight(DRIVE_SPEED, 17.0, -45.0);  // Drive Forward 17" at -45 degrees (12"x and 12"y)
                turnToHeading(TURN_SPEED, 45.0);               // Turn  CCW  to  45 Degrees
                holdHeading(TURN_SPEED, 45.0, 0.5);    // Hold  45 Deg heading for a 1/2 second

                driveStraight(DRIVE_SPEED, 17.0, 45.0);  // Drive Forward 17" at 45 degrees (-12"x and 12"y)
                turnToHeading(TURN_SPEED, 0.0);               // Turn  CW  to 0 Degrees
                holdHeading(TURN_SPEED, 0.0, 1.0);    // Hold  0 Deg heading for 1 second

                driveStraight(DRIVE_SPEED, -48.0, 0.0);    // Drive in Reverse 48" (should return to approx. staring position)

                telemetry.addData("Path", "Complete");
                telemetry.update();
                sleep(1000);  // Pause to display last telemetry message.

                */




            }
        }
    }
}
