// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;
import com.revrobotics.RelativeEncoder;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.cameraserver.CameraServer;

/**
 * CAN IDS:
 * LeftFrontDriveMotor: 2
 * LeftRearDriveMotor: 3
 * RightFrontDriveMotor: 4
 * RightRearDriveMotor: 5
 * elevator: 6
 * arm: 7
 * intake: 8
 */
public class Robot extends TimedRobot {

  //setting up auto choices
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  //controllers
  public final XboxController driver = new XboxController(0);
  public final XboxController operator = new XboxController(1);


  //Drive Train
  private final CANSparkMax m_LeftFrontDriveMotor = new CANSparkMax(2, MotorType.kBrushless);
  private final CANSparkMax m_LeftRearDriveMotor = new CANSparkMax(3, MotorType.kBrushless);
  private final CANSparkMax m_RightFrontDriveMotor = new CANSparkMax(4, MotorType.kBrushless);
  private final CANSparkMax m_RightRearDriveMotor = new CANSparkMax(5, MotorType.kBrushless);
  private final MotorControllerGroup m_left = new MotorControllerGroup(m_LeftFrontDriveMotor,m_LeftRearDriveMotor);
  private final MotorControllerGroup m_right = new MotorControllerGroup(m_RightFrontDriveMotor,m_RightRearDriveMotor);
  private DifferentialDrive m_myRobot = new DifferentialDrive(m_left, m_right);

  //Elevator
  private final CANSparkMax elevator = new CANSparkMax (6, MotorType.kBrushless);
  RelativeEncoder elevatEncoder = elevator.getEncoder();

  //Arm
  private final CANSparkMax arm = new CANSparkMax(7, MotorType.kBrushless);
  RelativeEncoder armEncoder = arm.getEncoder();

  //Intake
  private final CANSparkMax intake = new CANSparkMax(8, MotorType.kBrushless);

  //set up timer
  Timer timer = new Timer();


  @Override
  public void robotInit() {
    m_right.setInverted(true);
    CameraServer.startAutomaticCapture();
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    
  }

  //set drive train to break mode
  public void breakmode()
  {
    m_LeftFrontDriveMotor.setIdleMode(IdleMode.kBrake);
    m_LeftRearDriveMotor.setIdleMode(IdleMode.kBrake);
    m_RightFrontDriveMotor.setIdleMode(IdleMode.kBrake);
    m_RightRearDriveMotor.setIdleMode(IdleMode.kBrake);
  }

  //set drive train to coast mode
  public void coastmode()
  {
    m_LeftFrontDriveMotor.setIdleMode(IdleMode.kCoast);
    m_LeftRearDriveMotor.setIdleMode(IdleMode.kCoast);
    m_RightFrontDriveMotor.setIdleMode(IdleMode.kCoast);
    m_RightRearDriveMotor.setIdleMode(IdleMode.kCoast);
  }

  //move intake in different directions depending on trigger
  public void intake(){
    intake.set(operator.getLeftTriggerAxis());
    intake.set(-operator.getRightTriggerAxis());
  }

  //you can move the arm if you press X and then move the left joystick
  public void arm(){
    if (operator.getRawButton(3)){
      arm.set(operator.getLeftY());
    }
  }

  public void lowLevel(){
    elevatEncoder.setPosition(kDefaultPeriod);
    armEncoder.setPosition(kDefaultPeriod);
  }

  public void midLevel(){
    elevatEncoder.setPosition(kDefaultPeriod);
    armEncoder.setPosition(kDefaultPeriod);

  }

  public void topLevel(){
    elevatEncoder.setPosition(kDefaultPeriod);
    armEncoder.setPosition(kDefaultPeriod);

  }


  public void elevator(){
    //hold Y to go to top level
    if (operator.getRawButton(4)){
      topLevel();
    }
    //hold B to go to mid level
    else if (operator.getRawButton(2))
    {
      midLevel();
    }
    //hold A to go to low level
    else if (operator.getRawButton(1))
    {
      lowLevel();
    }

  }

  @Override
  public void teleopPeriodic() {
    m_myRobot.tankDrive(-driver.getLeftY()*0.75, -driver.getRightY()*0.75);
    intake();
    arm();
    elevator();
  }

  @Override
  public void autonomousInit()
  {
    m_autoSelected = m_chooser.getSelected();
    System.out.println("Auto selected: " + m_autoSelected);
    timer.reset();
    timer.start();
  }

  

  @Override
  public void autonomousPeriodic()
  {
    switch (m_autoSelected) {
      case kCustomAuto:
      if (timer.get()<1){
        coastmode();
        //m_myRobot.tankDrive(-0.5, 0.65);
        m_myRobot.tankDrive(0.5, 0.5);
      }
      else if (timer.get() < 1.5){
        m_myRobot.tankDrive(0.5, -0.5);
        
      }
      else if (timer.get() < 2){
        m_myRobot.tankDrive(0.5, 0.5);
      }
      else{
        m_myRobot.tankDrive(0, 0);
        breakmode();
      }
        break;
      case kDefaultAuto:
      default:
      if (timer.get()<1)
      {
        coastmode();
        m_myRobot.tankDrive(0.5, 0.5);
      }
      else{
        m_myRobot.tankDrive(0, 0);
        breakmode();
      }
        break;
    }
    }
    
  }

