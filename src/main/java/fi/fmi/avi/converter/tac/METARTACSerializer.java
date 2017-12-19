package fi.fmi.avi.converter.tac;

import static fi.fmi.avi.converter.tac.lexer.Lexeme.Identity;
import fi.fmi.avi.converter.ConversionResult;
import fi.fmi.avi.converter.tac.lexer.Lexeme;
import fi.fmi.avi.converter.tac.lexer.LexemeSequence;
import fi.fmi.avi.converter.tac.lexer.LexemeSequenceBuilder;
import fi.fmi.avi.converter.tac.lexer.SerializingException;
import fi.fmi.avi.model.AviationWeatherMessage;
import fi.fmi.avi.model.CloudForecast;
import fi.fmi.avi.model.Weather;
import fi.fmi.avi.model.metar.METAR;
import fi.fmi.avi.model.metar.ObservedClouds;
import fi.fmi.avi.model.metar.RunwayState;
import fi.fmi.avi.model.metar.RunwayVisualRange;
import fi.fmi.avi.model.metar.TrendForecast;
import fi.fmi.avi.converter.ConversionHints;
import fi.fmi.avi.converter.ConversionIssue;
import fi.fmi.avi.converter.ConversionIssue.Type;
/**
 * Created by rinne on 07/06/17.
 */
public class METARTACSerializer extends AbstractTACSerializer<METAR> {

    @Override
    public ConversionResult<String> convertMessage(final METAR input, final ConversionHints hints) {
        ConversionResult<String> result = new ConversionResult<String>();
        try {
        	LexemeSequence seq = tokenizeMessage(input, hints);
        	result.setConvertedMessage(seq.getTAC());
        } catch (SerializingException se) {
        	result.addIssue(new ConversionIssue(Type.OTHER, se.getMessage()));
        }
    	return result;
    }

    @Override
    public LexemeSequence tokenizeMessage(final AviationWeatherMessage msg) throws SerializingException {
        return tokenizeMessage(msg, null);
    }

    @Override
    public LexemeSequence tokenizeMessage(final AviationWeatherMessage msg, final ConversionHints hints) throws SerializingException {
        if (!(msg instanceof METAR)) {
            throw new SerializingException("I can only tokenize METARs!");
        }
        METAR input = (METAR) msg;
        LexemeSequenceBuilder retval = this.getLexingFactory().createLexemeSequenceBuilder();
        appendToken(retval, Identity.METAR_START, input, METAR.class, hints);
        appendWhitespace(retval, ' ', hints);
        if (appendToken(retval, Identity.CORRECTION, input, METAR.class, hints) > 0) {
            appendWhitespace(retval, ' ', hints);
        }
        if (appendToken(retval, Identity.AERODROME_DESIGNATOR, input, METAR.class, hints) > 0) {
            appendWhitespace(retval, ' ', hints);
        }
        if (appendToken(retval, Identity.ISSUE_TIME, input, METAR.class, hints) > 0) {
            appendWhitespace(retval, ' ', hints);
        }

        if (appendToken(retval, Identity.NIL, input, METAR.class, hints) > 0) {
            appendToken(retval, Identity.END_TOKEN, input, METAR.class, hints);
            return retval.build();
        }

        if (appendToken(retval, Identity.AUTOMATED, input, METAR.class, hints) > 0) {
            appendWhitespace(retval, ' ', hints);
        }
        if (appendToken(retval, Identity.SURFACE_WIND, input, METAR.class, hints) > 0) {
            appendWhitespace(retval, ' ', hints);
        }
        if (appendToken(retval, Identity.VARIABLE_WIND_DIRECTION, input, METAR.class, hints) > 0) {
            appendWhitespace(retval, ' ', hints);
        }
        if (appendToken(retval, Identity.CAVOK, input, METAR.class, hints) > 0) {
            appendWhitespace(retval, ' ', hints);
        }
        if (appendToken(retval, Identity.HORIZONTAL_VISIBILITY, input, METAR.class, hints) > 0) {
            appendWhitespace(retval, ' ', hints);
        }
        if (input.getRunwayVisualRanges() != null) {
            for (RunwayVisualRange range : input.getRunwayVisualRanges()) {
                appendToken(retval, Identity.RUNWAY_VISUAL_RANGE, input, METAR.class, hints, range);
                appendWhitespace(retval, ' ', hints);
            }
        }
        if (input.getPresentWeather() != null) {
            for (Weather weather : input.getPresentWeather()) {
                appendToken(retval, Identity.WEATHER, input, METAR.class, hints, weather);
                appendWhitespace(retval, ' ', hints);
            }
        }
        ObservedClouds obsClouds = input.getClouds();
        if (obsClouds != null) {
            if (obsClouds.getVerticalVisibility() != null) {
                this.appendToken(retval, Lexeme.Identity.CLOUD, input, METAR.class, hints, "VV");
                appendWhitespace(retval, ' ', hints);
            } else if (obsClouds.isAmountAndHeightUnobservableByAutoSystem()) {
                this.appendToken(retval, Lexeme.Identity.CLOUD, input, METAR.class, hints, "//////");
                appendWhitespace(retval, ' ', hints);
            } else if (obsClouds.isNoSignificantCloud()) {
            	this.appendToken(retval, Lexeme.Identity.CLOUD, input, METAR.class, hints, "NSC");
                appendWhitespace(retval, ' ', hints);
            } else {
                this.appendCloudLayers(retval, input, METAR.class, obsClouds.getLayers(), hints);
            }
        }
        if (appendToken(retval, Identity.AIR_DEWPOINT_TEMPERATURE, input, METAR.class, hints) > 0) {
            appendWhitespace(retval, ' ', hints);
        }
        if (appendToken(retval, Identity.AIR_PRESSURE_QNH, input, METAR.class, hints) > 0) {
            appendWhitespace(retval, ' ', hints);
        }
        if (input.getRecentWeather() != null) {
            for (Weather weather : input.getRecentWeather()) {
                appendToken(retval, Identity.RECENT_WEATHER, input, METAR.class, hints, weather);
                appendWhitespace(retval, ' ', hints);
            }
        }
        if (appendToken(retval, Identity.WIND_SHEAR, input, METAR.class, hints) > 0) {
            appendWhitespace(retval, ' ', hints);
        }
        if (appendToken(retval, Identity.SEA_STATE, input, METAR.class, hints) > 0) {
            appendWhitespace(retval, ' ', hints);
        }
        if (input.getRunwayStates() != null) {
            for (RunwayState state : input.getRunwayStates()) {
                appendToken(retval, Identity.RUNWAY_STATE, input, METAR.class, hints, state);
                appendWhitespace(retval, ' ', hints);
            }
        }
        if (appendToken(retval, Identity.NO_SIGNIFICANT_WEATHER, input, METAR.class, hints) > 0) {
            appendWhitespace(retval, ' ', hints);
        }
        if (appendToken(retval, Identity.COLOR_CODE, input, METAR.class, hints) > 0) {
            appendWhitespace(retval, ' ', hints);
        }
        if (input.getTrends() != null) {
            for (TrendForecast trend : input.getTrends()) {
                if (appendToken(retval, Identity.TREND_CHANGE_INDICATOR, input, METAR.class, hints, trend) > 0) {
                    appendWhitespace(retval, ' ', hints);
                }
                if (appendToken(retval, Identity.TREND_TIME_GROUP, input, METAR.class, hints, trend) > 0) {
                    appendWhitespace(retval, ' ', hints);
                }
                if (appendToken(retval, Identity.SURFACE_WIND, input, METAR.class, hints, trend) > 0) {
                    appendWhitespace(retval, ' ', hints);
                }
                if (appendToken(retval, Identity.CAVOK, input, METAR.class, hints, trend) > 0) {
                    appendWhitespace(retval, ' ', hints);
                }
                if (appendToken(retval, Identity.NO_SIGNIFICANT_WEATHER, input, METAR.class, hints, trend) > 0) {
                    appendWhitespace(retval, ' ', hints);
                }
                if (appendToken(retval, Identity.HORIZONTAL_VISIBILITY, input, METAR.class, hints, trend) > 0) {
                    appendWhitespace(retval, ' ', hints);
                }
                if (trend.getForecastWeather() != null) {
                    for (Weather weather : trend.getForecastWeather()) {
                        appendToken(retval, Identity.WEATHER, input, METAR.class, hints, trend, weather);
                        appendWhitespace(retval, ' ', hints);
                    }
                }
                
                CloudForecast clouds = trend.getCloud();
                if (clouds != null) {
                    if (clouds.getVerticalVisibility() != null) {
                        this.appendToken(retval, Lexeme.Identity.CLOUD, input, METAR.class, hints, "VV", trend);
                        appendWhitespace(retval, ' ', hints);
                    } else if (clouds.isNoSignificantCloud()) {
                    	this.appendToken(retval, Lexeme.Identity.CLOUD, input, METAR.class, hints, trend);
                        appendWhitespace(retval, ' ', hints);
                    } else {
                        this.appendCloudLayers(retval, input, METAR.class, clouds.getLayers(), hints, trend);
                    }
                }
                if (appendToken(retval, Identity.COLOR_CODE, input, METAR.class, hints, trend) > 0) {
                    appendWhitespace(retval, ' ', hints);
                }
            }
        }
        if (input.getRemarks() != null && !input.getRemarks().isEmpty()) {
            appendToken(retval, Identity.REMARKS_START, input, METAR.class, hints);
            appendWhitespace(retval, ' ', hints);
            for (String remark : input.getRemarks()) {
                this.appendToken(retval, Identity.REMARK, input, METAR.class, hints, remark);
                appendWhitespace(retval, ' ', hints);
            }
        }
        retval.removeLast();
        appendToken(retval, Identity.END_TOKEN, input, METAR.class, hints);
        return retval.build();
    }
}

