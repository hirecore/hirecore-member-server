package io.hirecore.hirecorememberserver.modules.portfolio.domain.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioDomainException;
import io.hirecore.hirecorememberserver.modules.portfolio.domain.exception.PortfolioDomainExceptionCodeCluster;

public enum PortfolioType {
    TEAM("team"), PERSONAL("personal");

    private final String type;

    PortfolioType(String type) {
        this.type = type;
    }

    @JsonCreator
    public static PortfolioType from(String type) {
        for (PortfolioType portfolioType : values()) {
            if (portfolioType.type.equalsIgnoreCase(type)) {
                return portfolioType;
            }
        }
        throw new PortfolioDomainException(
                PortfolioDomainExceptionCodeCluster.HiddenDetailResponse.UNSUPPORTED_PORTFOLIO_TYPE
        );
    }

    @JsonValue
    public String getType() {
        return type;
    }
}
