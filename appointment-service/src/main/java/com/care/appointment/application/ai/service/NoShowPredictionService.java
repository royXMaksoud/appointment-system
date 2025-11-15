package com.care.appointment.application.ai.service;

import com.care.appointment.domain.model.ai.ModelVersion;
import com.care.appointment.domain.model.ai.PredictionResult;
import com.care.appointment.infrastructure.db.repositories.ModelVersionRepository;
import com.care.appointment.infrastructure.db.repositories.PredictionResultRepository;
import com.care.appointment.web.dto.ai.PredictionRequest;
import com.care.appointment.web.dto.ai.PredictionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for predicting appointment no-show risk
 * Uses the active model to generate risk assessments
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoShowPredictionService {

    private final ModelVersionRepository modelVersionRepository;
    private final PredictionResultRepository predictionResultRepository;

    /**
     * Predict no-show risk for an appointment
     */
    @Transactional
    public PredictionResponse predict(UUID appointmentId) {
        log.info("Predicting no-show risk for appointment: {}", appointmentId);

        // TODO: Load appointment data and extract features
        // This will be implemented when we have access to Appointment entity

        throw new UnsupportedOperationException(
            "Prediction service requires AppointmentRepository integration - to be implemented");
    }

    /**
     * Predict with manually provided features
     */
    @Transactional
    public PredictionResponse predictWithManualFeatures(PredictionRequest request) {
        log.info("Predicting with manual features");

        // Get active model
        Optional<ModelVersion> activeModel = modelVersionRepository.findActiveModel();
        if (activeModel.isEmpty()) {
            throw new IllegalStateException("No active model found. Please train a model first.");
        }

        ModelVersion model = activeModel.get();

        // Extract features from request
        double[] features = extractFeaturesFromRequest(request);

        // Calculate risk score using simplified algorithm
        // (In production, this would use the actual trained model)
        BigDecimal riskScore = calculateRiskScore(features, request);
        PredictionResult.RiskLevel riskLevel = determineRiskLevel(riskScore);
        BigDecimal confidence = calculateConfidence(features);

        // Identify contributing factors
        List<PredictionResponse.ContributingFactor> factors =
            identifyContributingFactors(features, request);

        // Generate recommendations
        List<String> recommendations = generateRecommendations(riskLevel, factors);

        // Save prediction result
        PredictionResult predictionResult = PredictionResult.builder()
            .modelVersion(model)
            .appointmentId(request.getAppointmentId())
            .predictedRiskScore(riskScore)
            .predictedRiskLevel(riskLevel)
            .confidenceScore(confidence)
            .build();

        predictionResultRepository.save(predictionResult);

        // Build response
        return PredictionResponse.builder()
            .predictionId(predictionResult.getPredictionId())
            .appointmentId(request.getAppointmentId())
            .riskScore(riskScore)
            .riskLevel(riskLevel.name())
            .confidence(confidence)
            .contributingFactors(factors)
            .recommendedActions(recommendations)
            .predictionTimestamp(Instant.now())
            .modelVersion(model.getModelName() + " " + model.getVersionNumber())
            .build();
    }

    /**
     * Extract feature vector from request
     */
    private double[] extractFeaturesFromRequest(PredictionRequest request) {
        // Features array will be in this order:
        // [age, gender_encoded, service_type_encoded, time_encoded, day_encoded, distance, priority_encoded, prev_no_shows, prev_appointments]
        double[] features = new double[9];

        features[0] = request.getAge() != null ? request.getAge() : 0;
        features[1] = encodeGender(request.getGender());
        features[2] = encodeServiceType(request.getServiceType());
        features[3] = encodeTime(request.getAppointmentTime());
        features[4] = encodeDayOfWeek(request.getDayOfWeek());
        features[5] = request.getDistanceKm() != null ? request.getDistanceKm().doubleValue() : 0;
        features[6] = encodePriority(request.getPriority());
        features[7] = request.getPreviousNoShows() != null ? request.getPreviousNoShows() : 0;
        features[8] = request.getPreviousAppointments() != null ? request.getPreviousAppointments() : 0;

        return features;
    }

    /**
     * Simple risk score calculation based on feature analysis
     * In production, this would use a trained ML model (Random Forest, etc.)
     */
    private BigDecimal calculateRiskScore(double[] features, PredictionRequest request) {
        double baseScore = 0.0;

        // Previous no-shows are strong indicator
        if (request.getPreviousNoShows() != null && request.getPreviousNoShows() > 0) {
            baseScore += (request.getPreviousNoShows() * 0.15); // 15% per no-show
        }

        // Morning appointments have lower no-show rate
        if ("10:30".equals(request.getAppointmentTime()) ||
            "11:00".equals(request.getAppointmentTime())) {
            baseScore += 0.12; // Morning slots increase risk slightly
        }

        // Midweek (Wed-Thu) have higher no-show rates
        if ("WEDNESDAY".equals(request.getDayOfWeek()) || "THURSDAY".equals(request.getDayOfWeek())) {
            baseScore += 0.08;
        }

        // Age 18-30 has higher no-show rate
        if (request.getAge() >= 18 && request.getAge() <= 30) {
            baseScore += 0.10;
        }

        // Some service types have higher no-show rates
        if ("CARDIOLOGY".equals(request.getServiceType()) ||
            "GYNECOLOGY".equals(request.getServiceType())) {
            baseScore += 0.05;
        }

        // Distance increases no-show likelihood slightly
        if (request.getDistanceKm() != null && request.getDistanceKm().doubleValue() > 5) {
            baseScore -= 0.02; // Farther distance slightly reduces risk (more motivated)
        }

        // Cap at 0.95 to avoid extreme scores
        double finalScore = Math.min(baseScore, 0.95);
        finalScore = Math.max(finalScore, 0.05); // Minimum 5%

        return new BigDecimal(String.format("%.4f", finalScore));
    }

    /**
     * Determine risk level from score
     */
    private PredictionResult.RiskLevel determineRiskLevel(BigDecimal riskScore) {
        double score = riskScore.doubleValue();
        if (score >= 0.60) {
            return PredictionResult.RiskLevel.HIGH;
        } else if (score >= 0.40) {
            return PredictionResult.RiskLevel.MEDIUM;
        } else {
            return PredictionResult.RiskLevel.LOW;
        }
    }

    /**
     * Calculate confidence score based on data completeness
     */
    private BigDecimal calculateConfidence(double[] features) {
        // Count non-zero features
        int nonZeroFeatures = 0;
        for (double f : features) {
            if (f != 0) nonZeroFeatures++;
        }

        // Confidence is based on data completeness
        double confidence = 0.65 + (nonZeroFeatures * 0.04); // 65% to 100%
        confidence = Math.min(confidence, 0.95);

        return new BigDecimal(String.format("%.4f", confidence));
    }

    /**
     * Identify which factors contributed most to the risk score
     */
    private List<PredictionResponse.ContributingFactor> identifyContributingFactors(
        double[] features, PredictionRequest request) {

        List<PredictionResponse.ContributingFactor> factors = new ArrayList<>();

        // Factor 1: Previous no-shows
        if (request.getPreviousNoShows() != null && request.getPreviousNoShows() > 0) {
            factors.add(PredictionResponse.ContributingFactor.builder()
                .factor("Previous No-Shows")
                .value(String.valueOf(request.getPreviousNoShows()))
                .impactPercent(15)
                .description("كان عنده عدم حضور سابق - هذا يزيد احتمال عدم الحضور")
                .build());
        }

        // Factor 2: Appointment time
        if (request.getAppointmentTime() != null) {
            boolean isMorning = request.getAppointmentTime().startsWith("08") ||
                               request.getAppointmentTime().startsWith("09") ||
                               request.getAppointmentTime().startsWith("10");
            factors.add(PredictionResponse.ContributingFactor.builder()
                .factor("Appointment Time")
                .value(request.getAppointmentTime())
                .impactPercent(isMorning ? -5 : 12)
                .description(isMorning ? "الصباح البكري له معدل التزام أفضل" :
                           "المواعيد المتأخرة تحتاج مراقبة")
                .build());
        }

        // Factor 3: Day of week
        if (request.getDayOfWeek() != null) {
            boolean isWeekend = "FRIDAY".equals(request.getDayOfWeek()) ||
                               "SATURDAY".equals(request.getDayOfWeek());
            factors.add(PredictionResponse.ContributingFactor.builder()
                .factor("Day of Week")
                .value(request.getDayOfWeek())
                .impactPercent(isWeekend ? -8 : 8)
                .description(isWeekend ? "عطل نهاية الأسبوع = معدل حضور أعلى" :
                           "الأيام الأسبوعية الوسطى = مخاطر أعلى")
                .build());
        }

        // Factor 4: Age group
        if (request.getAge() != null) {
            factors.add(PredictionResponse.ContributingFactor.builder()
                .factor("Age Group")
                .value(request.getAge() + " years")
                .impactPercent(getAgeImpact(request.getAge()))
                .description(getAgeDescription(request.getAge()))
                .build());
        }

        // Factor 5: Distance
        if (request.getDistanceKm() != null) {
            factors.add(PredictionResponse.ContributingFactor.builder()
                .factor("Distance from Home")
                .value(request.getDistanceKm() + " km")
                .impactPercent(request.getDistanceKm().doubleValue() > 5 ? -2 : 0)
                .description("المسافة القريبة = احتمال حضور أعلى")
                .build());
        }

        return factors;
    }

    /**
     * Generate actionable recommendations
     */
    private List<String> generateRecommendations(
        PredictionResult.RiskLevel riskLevel,
        List<PredictionResponse.ContributingFactor> factors) {

        List<String> recommendations = new ArrayList<>();

        if (riskLevel == PredictionResult.RiskLevel.HIGH) {
            recommendations.add("send_sms_reminder_24h");
            recommendations.add("call_beneficiary_12h");
            recommendations.add("have_standby_list_ready");
            recommendations.add("prepare_alternative_appointment");
        } else if (riskLevel == PredictionResult.RiskLevel.MEDIUM) {
            recommendations.add("send_sms_reminder_24h");
            recommendations.add("send_email_reminder");
        } else {
            recommendations.add("send_email_reminder");
        }

        return recommendations;
    }

    // Helper encoding methods
    private double encodeGender(String gender) {
        if (gender == null) return 0;
        return "FEMALE".equalsIgnoreCase(gender) ? 1.0 : 0.0;
    }

    private double encodeServiceType(String serviceType) {
        if (serviceType == null) return 0;
        // Simple encoding: higher values = higher no-show risk
        return switch (serviceType.toUpperCase()) {
            case "CARDIOLOGY" -> 0.8;
            case "GYNECOLOGY" -> 0.7;
            case "PEDIATRICS" -> 0.3;
            default -> 0.5;
        };
    }

    private double encodeTime(String time) {
        if (time == null) return 0.5;
        int hour = Integer.parseInt(time.split(":")[0]);
        // Morning hours (8-11) have lower no-show rates
        if (hour >= 8 && hour <= 11) return 0.3;
        // Afternoon (14-17) higher risk
        if (hour >= 14 && hour <= 17) return 0.7;
        return 0.5;
    }

    private double encodeDayOfWeek(String day) {
        if (day == null) return 0.5;
        return switch (day.toUpperCase()) {
            case "SUNDAY", "MONDAY", "TUESDAY" -> 0.4;
            case "WEDNESDAY", "THURSDAY" -> 0.8;
            case "FRIDAY", "SATURDAY" -> 0.3;
            default -> 0.5;
        };
    }

    private double encodePriority(String priority) {
        if (priority == null) return 0.5;
        // URGENT has lower no-show rate
        return "URGENT".equalsIgnoreCase(priority) ? 0.2 : 0.5;
    }

    private int getAgeImpact(int age) {
        if (age < 18) return 5;
        if (age <= 30) return 10;
        if (age <= 45) return 5;
        return 0;
    }

    private String getAgeDescription(int age) {
        if (age < 18) return "الأطفال = معدل حضور معقول";
        if (age <= 30) return "الشباب (18-30) = مخاطر أعلى";
        if (age <= 45) return "الفئة الوسطى = مخاطر متوسطة";
        return "الفئة العمرية الأكبر = معدل حضور أفضل";
    }
}
