// Aniket Sonika
//Code for FRC 1450
//Year: 2023

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
 * LeftFrontDriveMotor: 3
 * LeftRearDriveMotor: 2
 * RightFrontDriveMotor: 4
 * RightRearDriveMotor: 5
 * elevator: 6
 * arm: 7
 * intake: 8
 * 
 * Controller ID:
 * Driver: 0
 * Operator: 1
 */
public class Robot extends TimedRobot {

  //setting up auto choices

  //crosses community line
  private static final String kDefaultAuto = "Default";
  //goes up ramp
  private static final String kRamp = "Ramp Up";
  //puts 1 cone in middle height
  private static final String k1ConeMid = "Middle Cone";
  //puts 1 cube in middle height
  private static final String k1CubeMid = "Middle Cube";
  //puts 1 cone in low height
  private static final String k1ConeLow = "Low Cone";
  //puts 1 cube in low height
  private static final String k1CubeLow = "Low Cube";
  //puts 1 cube in low height
  private static final String k1CubeHigh = "High Cube";

  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  //controllers
  public final XboxController driver = new XboxController(0);
  public final XboxController operator = new XboxController(1);


  //Drive Train
  private final CANSparkMax m_LeftFrontDriveMotor = new CANSparkMax(3, MotorType.kBrushless);
  private final CANSparkMax m_LeftRearDriveMotor = new CANSparkMax(2, MotorType.kBrushless);
  private final CANSparkMax m_RightFrontDriveMotor = new CANSparkMax(4, MotorType.kBrushless);
  private final CANSparkMax m_RightRearDriveMotor = new CANSparkMax(5, MotorType.kBrushless);
  private final MotorControllerGroup m_left = new MotorControllerGroup(m_LeftFrontDriveMotor,m_LeftRearDriveMotor);
  private final MotorControllerGroup m_right = new MotorControllerGroup(m_RightFrontDriveMotor,m_RightRearDriveMotor);
  private DifferentialDrive m_myRobot = new DifferentialDrive(m_left, m_right);

  //Elevator
  private final CANSparkMax elevator = new CANSparkMax (6, MotorType.kBrushless);

  //Arm
  private final CANSparkMax arm = new CANSparkMax(7, MotorType.kBrushless);
  // RelativeEncoder armEncoder = arm.getEncoder();

  //Intake
  private final CANSparkMax intake = new CANSparkMax(8, MotorType.kBrushless);

  //set up timer
  Timer timer = new Timer();


  @Override
  public void robotInit() {
    m_right.setInverted(true);
    intake.setInverted(true);
    elevator.setIdleMode(IdleMode.kCoast);
    arm.setIdleMode(IdleMode.kBrake);
    CameraServer.startAutomaticCapture();
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("Ramp Up", kRamp);
    m_chooser.addOption("One High Cube", k1CubeHigh);
    m_chooser.addOption("One Middle Cone", k1ConeMid);
    m_chooser.addOption("One Middle Cube", k1CubeMid);
    m_chooser.addOption("One Low Cone", k1ConeLow);
    m_chooser.addOption("One Low Cube", k1CubeLow);
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



  @Override
  public void teleopPeriodic() {
    elevator.setIdleMode(IdleMode.kCoast);
    arm.setIdleMode(IdleMode.kBrake);
    m_myRobot.tankDrive(-driver.getLeftY()*0.75, -driver.getRightY()*0.75);
    if (operator.getXButtonPressed() == true)
    {
      intake.set(-0.5);
    }
    else if (operator.getXButtonReleased() == true)
    {
      intake.set(0);
    }
    if (operator.getBButtonPressed() == true)
    {
      intake.set(0.5);
    }
    else if (operator.getBButtonReleased() == true)
    {
      intake.set(0);
    }


     arm.set(-operator.getRightY()*0.4);


     if (operator.getRightBumperPressed() == true)
     {
       elevator.set(1);
     }
     else if (operator.getRightBumperReleased() == true)
     {
       elevator.set(0);
     }
     if (operator.getLeftBumperPressed() == true)
     {
       elevator.set(-1);
     }
     else if (operator.getLeftBumperReleased() == true)
     {
       elevator.set(0);
     }
    
    }

  @Override
  public void autonomousInit()
  {
    elevator.setIdleMode(IdleMode.kCoast);
    m_autoSelected = m_chooser.getSelected();
    System.out.println("Auto selected: " + m_autoSelected);
    timer.reset();
    timer.start();
  }

  

  @Override
  public void autonomousPeriodic()
  {
    switch (m_autoSelected) {
        // go up ramp
      case kRamp:
      if (timer.get()<1.8){
        coastmode();
        m_myRobot.tankDrive(0.5, 0.5);
      }
      else{
        m_myRobot.tankDrive(0, 0);
        breakmode();
      }

       //puts 1 cone in middle height
      case k1ConeMid:
      if (timer.get() < 4)
      {
        elevator.setIdleMode(IdleMode.kCoast);
        m_myRobot.stopMotor();
        elevator.set(0.75);
      }
      else 
      {
        elevator.set(0);
      }


        //puts 1 cube in middle height
      case k1CubeMid:
      if (timer.get() < 2)
      {
        breakmode();
        m_myRobot.tankDrive(0.45,0.45);
        intake.set(0);
        elevator.set(1);
        arm.setIdleMode(IdleMode.kBrake);
        arm.set(0.2);
      }
      else if (timer.get() <7)
      {
        m_myRobot.tankDrive(0.25,0.25);
        intake.set(0);
        elevator.set(1);
        arm.set(0.2);
      }
      else if (timer.get() < 9)
      {
        m_myRobot.tankDrive(0.0,0.0);
        elevator.set(0);
        intake.set(-0.5);
        arm.set(0.2);

      }
      else if (timer.get() < 10.5)
      {
        m_myRobot.tankDrive(-0.75,- 0.75);
        arm.set(0);
        intake.set(0);
        arm.setIdleMode(IdleMode.kBrake);

      }
      else if (timer.get() < 11)
      {
        m_myRobot.tankDrive(-0.35, -0.35);
        arm.setIdleMode(IdleMode.kBrake);

      }
      else 
      {
        breakmode();
        m_myRobot.tankDrive(0, 0);
        elevator.set(0);
        intake.set(0);
        arm.set(0);
      }

      //puts 1 cube in high height
      case k1CubeHigh:
      if (timer.get() < 2)
      {
        coastmode();
        elevator.set(1);
        arm.setIdleMode(IdleMode.kBrake);
      }
      else if (timer.get() <10)
      {
        breakmode();
        elevator.set(1);
      }
      else if (timer.get() < 11)
      {
        intake.set(-0.5);
      }
      else if (timer.get() < 12.5)
      {
        coastmode();
        m_myRobot.tankDrive(-0.75, -0.75);
        arm.setIdleMode(IdleMode.kBrake);
      }
      else if (timer.get() < 13)
      {
        m_myRobot.tankDrive(-0.35, -0.35);
        arm.setIdleMode(IdleMode.kBrake);

      }

      else 
      {
        breakmode();
        m_myRobot.tankDrive(0, 0);
        elevator.set(0);
        intake.set(0);
      }


        //puts 1 cone in low height
      case k1ConeLow:
      if (timer.get() < 1.0)
      {
        coastmode();
        elevator.set(0.3);
        arm.setIdleMode(IdleMode.kBrake);
        intake.set(0.2);
      }
      else 
      {
        breakmode();
        m_myRobot.tankDrive(0, 0);
        elevator.set(0);
        intake.set(0);
      }
      


        //puts 1 cube in low height and goes up ramp
      case k1CubeLow:
      if (timer.get() < 1.0)
      {
        breakmode();
        elevator.set(0.3);
        arm.setIdleMode(IdleMode.kBrake);
        intake.set(-0.2);
      }
      else 
      {
        elevator.set(0);
        intake.set(0);
      }
     

    
      //just crossed auto line
      case kDefaultAuto:
      default:
      if (timer.get()<1.5)
      {
        m_myRobot.tankDrive(0, 0);
      }
      else{
        m_myRobot.tankDrive(0, 0);
        breakmode();
      }
        break;
    }
    }
    
  }

