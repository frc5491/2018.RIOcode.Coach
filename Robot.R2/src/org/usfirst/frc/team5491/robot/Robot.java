/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team5491.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.cscore.CvSink;
import edu.wpi.cscore.CvSource;
import edu.wpi.cscore.UsbCamera;
//import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Spark;
import edu.wpi.first.wpilibj.Talon;
import edu.wpi.first.wpilibj.drive.MecanumDrive;
//import edu.wpi.first.wpilibj.Ultrasonic;
import edu.wpi.first.wpilibj.Timer;
import java.io.Console;
import org.opencv.core.Mat;
import edu.wpi.first.wpilibj.Compressor;
//import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.XboxController;

/**
 * This is a demo program showing how to use Mecanum control with the RobotDrive
 * class.
 */
public class Robot extends IterativeRobot {
	private static final int kFrontLeftChannel = 4;
	private static final int kRearLeftChannel = 3;
	private static final int kFrontRightChannel = 2;
	private static final int kRearRightChannel = 6;
	private static final int kJoystickChannel = 0;
	private MecanumDrive m_robotDrive;
	private Joystick jStick;
	private XboxController xStick;
	
	Timer timer = new Timer(); // primary game timer
	Timer gTimer = new Timer(); // secondary game timer
	//Thread visionThread;
	
	//Ultrasonic ultr1 = new Ultrasonic(9,8);
	//Ultrasonic ultr2 = new Ultrasonic(7,6);
	//Ultrasonic ultr3 = new Ultrasonic(5,4);
	
	Talon frontLeft = new Talon(kFrontLeftChannel);
	Talon rearLeft = new Talon(kRearLeftChannel);
	Talon frontRight = new Talon(kFrontRightChannel);
	Talon rearRight = new Talon(kRearRightChannel);
	
	Spark liftMotor = new Spark(1); // this motor controls the lift (y-cable to two motors)
	
	Solenoid gripper = new Solenoid(0); // single solenoid to grip and release a power cube
	
	/*DigitalInput PosSwitch0 = new DigitalInput(0);
	DigitalInput PosSwitch1 = new DigitalInput(1);*/
	
	double myTimer;
	double gripperTimer;
	int GameMode = 0;
	boolean setter0 = true;
	boolean setter1 = false;
	boolean setter2 = false;
	boolean setter3 = false;
	boolean setter4 = false;
	char RobotPos;
	
	// these variables are used in various places for troubleshooting
	String i0;
	String i1;
	String i2;
	String i3;
	String i4;
	String i5;
	String i6;
	String i7;
	String i8;
	String i9;
	String i10;
	String i11;
	String i12;

	// these variables are instantiated for use as mode flags
	
	String p0;
	String p1;
	String p2;
	String p3;
	String p4;
	String p5;
	String p6;
	String p7;
	String p8;
	String p9;
	String p10;
	String p11;
	String p12;
	
	
/*
 * 	(non-Javadoc)
 * @see edu.wpi.first.wpilibj.IterativeRobotBase#robotInit()
 * 
 * This code runs at boot-time
 * 
 */
	
	@Override
	public void robotInit() {	
		//ultr1.setAutomaticMode(true);
		//ultr2.setAutomaticMode(true);
		//ultr3.setAutomaticMode(true);
		
		/*if (PosSwitch0.get() == true && PosSwitch1.get() == true) {
			RobotPos = 'R';
		} else if (PosSwitch0.get() == false && PosSwitch1.get() == false) {
			RobotPos = 'L';
		} else if (PosSwitch0.get() == true && PosSwitch1.get() == false) {
			RobotPos = 'M';
		} else if (PosSwitch0.get() == false && PosSwitch1.get() == true) {
			RobotPos = 'B';
		}*/
		
		

		m_robotDrive = new MecanumDrive(frontLeft, rearLeft, frontRight, rearRight);

		jStick = new Joystick(0);
		xStick = new XboxController(1);
		
		UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
		// Set the resolution
		camera.setResolution(420, 360);
		camera.setFPS(45);
		
		/*visionThread = new Thread(() -> {
			// Get the UsbCamera from CameraServer
			UsbCamera camera = CameraServer.getInstance().startAutomaticCapture();
			// Set the resolution
			camera.setResolution(420, 360);
			camera.setFPS(45);
			// Get a CvSink. This will capture Mats from the camera
			CvSink cvSink = CameraServer.getInstance().getVideo();
			// Setup a CvSource. This will send images back to the Dashboard
			CvSource outputStream = CameraServer.getInstance().putVideo("Rectangle", 160, 120);
			// Mats are very memory expensive. Lets reuse this Mat.
			Mat mat = new Mat();
			// This cannot be 'true'. The program will never exit if it is. This
			// lets the robot stop this thread when restarting robot code or
			// deploying.
			while (!Thread.interrupted()) {
				// Tell the CvSink to grab a frame from the camera and put it
				// in the source mat.  If there is an error notify the output.
				if (cvSink.grabFrame(mat) == 0) {
					// Send the output the error.
					outputStream.notifyError(cvSink.getError());
					// skip the rest of the current iteration
					continue;
				}
				outputStream.putFrame(mat);
			}
		});
		visionThread.setDaemon(true);
		visionThread.start();*/
	}

	
/*
 * (non-Javadoc)
 * @see edu.wpi.first.wpilibj.IterativeRobotBase#autonomousInit()
 * 
 * This code runs once at the start of Autonomous
 * 
 */
	@Override
	public void autonomousInit() {
		timer.start();
		String gameData;
		gameData = DriverStation.getInstance().getGameSpecificMessage();
		if (gameData != null) {
			System.out.println(gameData);
			if (gameData.charAt(0) == 'L') {
				GameMode = 1;
			} else if (gameData.charAt(0) == 'R') {
				GameMode = 2;
			} else {
				GameMode = 3;
			}
		}
	}
	
	
/*
 * (non-Javadoc)
 * @see edu.wpi.first.wpilibj.IterativeRobotBase#autonomousPeriodic()
 * 
 * This code runs sequentially, in a loop until the autonomous period expires
 * 
 */
	
	@Override
	public void autonomousPeriodic() { 
		
		gripper.set(true); //gripper starts in the closed state
		
		//set up some timers to keep track of cycles
		myTimer = timer.get();
		gripperTimer = timer.get();
		
		if (gripperTimer > 2.5) {
			/* 
			 * Open the gripper claw after 2.5 seconds
			 */
			
			//gripper.set(false); //open the gripper
		}
		
		if (myTimer < 2.5) {
			
			//move the arm motor to horizontal-ish position
			
			
			//drive forward 2.5 seconds at 0.3 speed
			m_robotDrive.driveCartesian(0.0, 0.3, 0.0, 0.0); //consider X coefficient to compensate for right-handed list
		
		
		} else {
			m_robotDrive.driveCartesian(0.0, 0.0, 0.0, 0.0); //stop moving
			gripper.set(false); //open the gripper
		}
		
		
	}
	
	
/*
 * (non-Javadoc)
 * @see edu.wpi.first.wpilibj.IterativeRobotBase#teleopInit()
 * 
 * This code runs once at the beginning of the teleoperated period
 */
	
	@Override
	public void teleopInit() {
		p0 = ""; // lifter
		p1 = ""; // gripper
		p2 = ""; // turbo
	}
	
	
/*
 * (non-Javadoc)
 * @see edu.wpi.first.wpilibj.IterativeRobotBase#teleopPeriodic()
 * 
 * This code runs periodically in the teleoperated period until the period expires.
 * 
 */
	
	@Override
	public void teleopPeriodic() {
		// Use the joystick X axis for lateral movement, Y axis for forward
		// movement, and Z axis for rotation.
		
		if (jStick.getRawButton(2) == true) { p2 = "turbo"; System.out.println("command: p2 turbo mode"); } else { p2 = ""; }

		
		double ZAxis = jStick.getZ();
		if (p2 == "turbo") { ZAxis = ZAxis / 2; } else { ZAxis = ZAxis / 3; }
		double YAxis = -jStick.getY();
		if (p2 == "turbo") { YAxis = YAxis / 2; } else { YAxis = YAxis / 3; }
		double XAxis = -jStick.getX();
		if (p2 == "turbo") { XAxis = XAxis / 2; } else { XAxis = XAxis / 3; }
		
		m_robotDrive.driveCartesian(XAxis, YAxis, ZAxis, 0.0);

		/*
		 * In iterative robot, we need to reset the variables each iteration.
		 * Do this work here...
		 */
		

		
		/*
		 * Activate the lifting arm using the joystick POV hat
		 * 
		 * (hat up - POV = 0)
		 * (hat down - POV = 180)
		 * 
		 */
		
		//if (jStick.getRawButton(11) == true) { p0 = "down"; } else if (jStick.getRawButton(9) == true) { p0 = "up"; }	else { p0 = ""; }
		if (jStick.getPOV() == 0) { p0 = "up"; } else if (jStick.getPOV() == 180) { p0 = "down"; } else { p0 = ""; }
		
		if (p0 == "up") { 
			
			System.out.println("command: p0 lift up");
			liftMotor.set(-0.6);}
		
		else if (p0 == "down") { 
			
			System.out.println("command: p0 lift down");
			liftMotor.set(0.6);}
		
		else { 
			System.out.println("command:");
			liftMotor.set(0.0); }
		
		if (jStick.getTrigger() == true) { gripper.set(true); System.out.println("command: p1 grip release"); } else { gripper.set(false); }
		//if (jStick.getRawButton(7) == true) { gripper.set(true); System.out.println("command: p1 grip release"); } else { gripper.set(false); }

		
		
		
		
		
		

		
		/* Activate the gripper solenoid via the
		 * trigger on the joystick.
		 */
		
		/*if (jStick.getTrigger()) {
			gripper.set(true);
		} else {
			gripper.set(false);
		}
		
		if (jStick.getPOV() == 0) {

		}
		*/
		
/*
		if (jStick.getRawButton(2) == true) { liftState = 1; }
		if (jStick.getrawbutton(3) == true) { liftState = 2; }
		if (jStick.getRawButton(2) == false && jStick.getRawButton(3) == false) { liftState = 0; }
		
		switch liftState {
		
		Case 0:
			liftMotor.setSpeed(0.25;
			
		Case 1:
			liftMotor.setSpeed(-0.25);
		Case 2:
		
		}
		
		if (jStick.getRawButton(2) == true) {
			liftMotor.setSpeed(0.25);
			} else if (jStick.getRawButton(3) == true) {
			liftMotor.setSpeed(-0.25);
		} 
		
		else {
			liftMotor.setSpeed(0.0);
		}
*/	
		
	}

	@Override
	
	public void testInit() {
		

	

	
	}
	
	public void testPeriodic() {
		
		i0 = "___";
		i1 = "___";
		i2 = "___";
		i3 = "___";
		i4 = "___";
		i5 = "___";
		i6 = "___";
		i7 = "___";
		i8 = "___";
		i9 = "___";
		i10 = "___";
		i11 = "___";
		i12 = "___";


		if (jStick.getRawButton(2) == true) { i2 = "i2"; } else { i1 = "___"; }
		if (jStick.getRawButton(3) == true) { i3 = "i3"; } else { i1 = "___"; }
		if (jStick.getRawButton(4) == true) { i4 = "i4"; } else { i1 = "___"; }
		if (jStick.getRawButton(5) == true) { i5 = "i5"; } else { i1 = "___"; }
		if (jStick.getRawButton(6) == true) { i6 = "i6"; } else { i1 = "___"; }
		if (jStick.getRawButton(7) == true) { i7 = "i7"; } else { i1 = "___"; }
		if (jStick.getRawButton(8) == true) { i8 = "i8"; } else { i1 = "___"; }
		if (jStick.getRawButton(9) == true) { i9 = "i9"; } else { i1 = "___"; }
		if (jStick.getRawButton(10) == true) { i10 = "i10"; } else { i1 = "___"; }
		if (jStick.getRawButton(11) == true) { i11 = "i11"; } else { i1 = "___"; }
		if (jStick.getRawButton(12) == true) { i12 = "i12"; } else { i1 = "___"; }
		if (jStick.getRawButton(0) == true) { i0 = "i0"; } else { i1 = "___"; }
		
		p0 = "___";
		p1 = "___";
		p2 = "___";
		p3 = "___";
		p4 = "___";
		p5 = "___";
		p6 = "___";
		p7 = "___";
		p8 = "___";
		p9 = "___";
		p10 = "___";
		p11 = "___";
		p12 = "___";
		
		if (jStick.getPOV() == 0) { p1 = "000"; } else { p1 = "___"; }
		if (jStick.getPOV() == 45) { p2 = "045"; } else { p2 = "___"; }
		if (jStick.getPOV() == 90) { p3 = "090"; } else { p3 = "___"; }
		if (jStick.getPOV() == 135) { p4 = "135"; } else { p4 = "___"; }
		if (jStick.getPOV() == 180) { p5 = "180"; } else { p5 = "___"; }
		if (jStick.getPOV() == 225) { p6 = "225"; } else { p6 = "___"; }
		if (jStick.getPOV() == 270) { p7 = "270"; } else { p7 = "___"; }
		if (jStick.getPOV() == 315) { p8 = "315"; } else { p8 = "___"; }
		

		
		System.out.println(i0 + " | " + i1 + " | " + i2 + " | " + i3 + " | " + i4 + " | " + i5 + " | " + i6 + " | " + i7 + " | " + i8 + " | " + i9 + " | " + i10 + " | " + i11 + " | " + i12);

		System.out.println(p0 + " | " + p1 + " | " + p2 + " | " + p3 + " | " + p4 + " | " + p5 + " | " + p6 + " | " + p7 + " | " + p8 + " | " + p9 + " | " + p10 + " | " + p11 + " | " + p12);
		
		if (jStick.getPOV() == 0) { p0 = "up"; }
		if (jStick.getPOV() == 180) { p0 = "down"; }
		
		if (p0 == "up") { System.out.println("up"); }
		if (p0 == "down") { System.out.println("down"); }
		
		if (jStick.getTrigger() == true) { System.out.println("open"); }

		//System.out.println(jStick.getX() & " -- " & jStick.getY() & " -- " & jStick.getZ & " -- ");
		
		//if (jStick.getPOV() == 0) {System.out.println("POV 0")}

		/*
		if (jStick.getRawButton(1) == true) { liftMotor.set(1.0); }
		if (jStick.getRawButton(2) == true) { liftMotor.set(-1.0); }
		*/
		

		}
		
		/*
		double ZAxis = jStick.getZ();
		ZAxis = ZAxis / 2;
		double YAxis = -jStick.getY();
		YAxis = YAxis / 2;
		double XAxis = -jStick.getX();
		XAxis = XAxis / 2;
		
		m_robotDrive.driveCartesian(XAxis, YAxis, ZAxis, 0.0);
		*/
	
	}
	
	

	
