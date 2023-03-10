// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.revrobotics.CANEncoder;
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
 * This is a demo program showing the use of the DifferentialDrive class, specifically it contains
 * the code necessary to operate a robot with tank drive.
 */
public class Robot extends TimedRobot {

  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  public final XboxController xbox = new XboxController(0);
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

  //Intake
  private final CANSparkMax intake = new CANSparkMax(8, MotorType.kBrushless);

  Timer timer = new Timer();


  @Override
  public void robotInit() {
    // We need to invert one side of the drivetrain so that positive voltages
    // result in both sides moving forward. Depending on how your robot's
    // gearbox is constructed, you might have to invert the left side instead.
    m_right.setInverted(true);
    
    CameraServer.startAutomaticCapture();
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    
  }

  public void intake(){
    intake.set(operator.getLeftTriggerAxis());
    intake.set(-operator.getRightTriggerAxis());
  }

  public void arm(){
    arm.set(operator.getLeftY());
  }

  public void lowLevel(){
    elevatEncoder.setPosition(kDefaultPeriod);
  }

  public void midLevel(){
    elevatEncoder.setPosition(kDefaultPeriod);
  }

  public void topLevel(){
    elevatEncoder.setPosition(kDefaultPeriod);
  }

  public void elevator(){
    if (operator.getRawButton(4)){
      topLevel();
    }

    else if (operator.getRawButton(2))
    {
      midLevel();
    }

    else if (operator.getRawButton(1))
    {
      lowLevel();
    }

  }

  @Override
  public void teleopPeriodic() {
    m_myRobot.tankDrive(-xbox.getLeftY()*0.75, -xbox.getRightY()*0.75);
    intake();
    arm();
  }

  @Override
  public void autonomousInit()
  {
    m_autoSelected = m_chooser.getSelected();
    System.out.println("Auto selected: " + m_autoSelected);
    timer.reset();
    timer.start();
  }

  public void breakmode()
  {
    m_LeftFrontDriveMotor.setIdleMode(IdleMode.kBrake);
    m_LeftRearDriveMotor.setIdleMode(IdleMode.kBrake);
    m_RightFrontDriveMotor.setIdleMode(IdleMode.kBrake);
    m_RightRearDriveMotor.setIdleMode(IdleMode.kBrake);
  }

  public void coastmode()
  {
    m_LeftFrontDriveMotor.setIdleMode(IdleMode.kCoast);
    m_LeftRearDriveMotor.setIdleMode(IdleMode.kCoast);
    m_RightFrontDriveMotor.setIdleMode(IdleMode.kCoast);
    m_RightRearDriveMotor.setIdleMode(IdleMode.kCoast);
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

