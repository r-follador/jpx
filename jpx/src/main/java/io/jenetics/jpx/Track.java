/*
 * Java GPX Library (@__identifier__@).
 * Copyright (c) @__year__@ Franz Wilhelmstötter
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author:
 *    Franz Wilhelmstötter (franz.wilhelmstoetter@gmail.com)
 */
package jpx;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static jpx.Lists.immutable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Represents a GPX track - an ordered list of points describing a path.
 * <p>
 * Creating a Track object with one track-segment and 3 track-points:
 * <pre>{@code
 * final Track track = Track.builder()
 *     .name("Track 1")
 *     .description("Mountain bike tour.")
 *     .addSegment(segment -> segment
 *         .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(160))
 *         .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(161))
 *         .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(162))))
 *     .addSegment(segment -> segment
 *         .addPoint(p -> p.lat(46.2081743).lon(16.3738189).ele(160))
 *         .addPoint(p -> p.lat(47.2081743).lon(16.3738189).ele(161))
 *         .addPoint(p -> p.lat(49.2081743).lon(16.3738189).ele(162))))
 *     .build();
 * }</pre>
 *
 * @author <a href="mailto:franz.wilhelmstoetter@gmail.com">Franz Wilhelmstötter</a>
 * @version 1.0
 * @since 1.0
 */
public final class Track implements Iterable<TrackSegment>, Serializable {

	private static final long serialVersionUID = 1L;

	private final String _name;
	private final String _comment;
	private final String _description;
	private final String _source;
	private final List<Link> _links;
	private final UInt _number;
	private final String _type;
	private final List<TrackSegment> _segments;

	/**
	 * Create a new {@code Track} with the given parameters.
	 *
	 * @param name the GPS name of the track
	 * @param comment the GPS comment for the track
	 * @param description user description of the track
	 * @param source the source of data. Included to give user some idea of
	 *        reliability and accuracy of data.
	 * @param links the links to external information about track
	 * @param number the GPS track number
	 * @param type the type (classification) of track
	 * @param segments the track-segments holds a list of track-points which are
	 *        logically connected in order. To represent a single GPS track
	 *        where GPS reception was lost, or the GPS receiver was turned off,
	 *        start a new track-segment for each continuous span of track data.
	 */
	private Track(
		final String name,
		final String comment,
		final String description,
		final String source,
		final List<Link> links,
		final UInt number,
		final String type,
		final List<TrackSegment> segments
	) {
		_name = name;
		_comment = comment;
		_description = description;
		_source = source;
		_links = immutable(links);
		_number = number;
		_type = type;
		_segments = immutable(segments);
	}

	/**
	 * Return the track name.
	 *
	 * @return the track name
	 */
	public Optional<String> getName() {
		return Optional.ofNullable(_name);
	}

	/**
	 * Return the GPS comment of the track.
	 *
	 * @return the GPS comment of the track
	 */
	public Optional<String> getComment() {
		return Optional.ofNullable(_comment);
	}

	/**
	 * Return the text description of the track.
	 *
	 * @return the text description of the track
	 */
	public Optional<String> getDescription() {
		return Optional.ofNullable(_description);
	}

	/**
	 * Return the source of data. Included to give user some idea of reliability
	 * and accuracy of data.
	 *
	 * @return the source of data
	 */
	public Optional<String> getSource() {
		return Optional.ofNullable(_source);
	}

	/**
	 * Return the links to external information about the track.
	 *
	 * @return the links to external information about the track
	 */
	public List<Link> getLinks() {
		return _links;
	}

	/**
	 * Return the GPS track number.
	 *
	 * @return the GPS track number
	 */
	public Optional<UInt> getNumber() {
		return Optional.ofNullable(_number);
	}

	/**
	 * Return the type (classification) of the track.
	 *
	 * @return the type (classification) of the track
	 */
	public Optional<String> getType() {
		return Optional.ofNullable(_type);
	}

	/**
	 * Return the sequence of route points.
	 *
	 * @return the sequence of route points
	 */
	public List<TrackSegment> getSegments() {
		return _segments;
	}

	/**
	 * Return a stream of {@link TrackSegment} objects this track contains.
	 *
	 * @return a stream of {@link TrackSegment} objects this track contains
	 */
	public Stream<TrackSegment> segments() {
		return _segments.stream();
	}

	@Override
	public Iterator<TrackSegment> iterator() {
		return _segments.iterator();
	}


	@Override
	public int hashCode() {
		int hash = 31;
		hash += 17*Objects.hashCode(_name) + 37;
		hash += 17*Objects.hashCode(_comment) + 37;
		hash += 17*Objects.hashCode(_description) + 37;
		hash += 17*Objects.hashCode(_source) + 37;
		hash += 17*Objects.hashCode(_links) + 37;
		hash += 17*Objects.hashCode(_number) + 37;
		hash += 17*Objects.hashCode(_segments) + 37;

		return hash;
	}

	@Override
	public boolean equals(final Object obj) {
		return obj instanceof Track &&
			Objects.equals(((Track)obj)._name, _name) &&
			Objects.equals(((Track)obj)._comment, _comment) &&
			Objects.equals(((Track)obj)._description, _description) &&
			Objects.equals(((Track)obj)._source, _source) &&
			Objects.equals(((Track)obj)._links, _links) &&
			Objects.equals(((Track)obj)._number, _number) &&
			Objects.equals(((Track)obj)._segments, _segments);
	}

	@Override
	public String toString() {
		return format("Track[name=%s, segments=%s]", _name, _segments);
	}

	/**
	 * Builder class for creating immutable {@code Track} objects.
	 * <p>
	 * Creating a Track object with one track-segment and 3 track-points:
	 * <pre>{@code
	 * final Track track = Track.builder()
	 *     .name("Track 1")
	 *     .description("Mountain bike tour.")
	 *     .addSegment(segment -> segment
	 *         .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(160))
	 *         .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(161))
	 *         .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(162))))
	 *     .addSegment(segment -> segment
	 *         .addPoint(p -> p.lat(46.2081743).lon(16.3738189).ele(160))
	 *         .addPoint(p -> p.lat(47.2081743).lon(16.3738189).ele(161))
	 *         .addPoint(p -> p.lat(49.2081743).lon(16.3738189).ele(162))))
	 *     .build();
	 * }</pre>
	 */
	public static final class Builder {
		private String _name;
		private String _comment;
		private String _description;
		private String _source;
		private List<Link> _links;
		private UInt _number;
		private String _type;
		private List<TrackSegment> _segments;

		private Builder() {
		}

		/**
		 * Set the name of the track.
		 *
		 * @param name the track name
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder name(final String name) {
			_name = name;
			return this;
		}

		/**
		 * Set the comment of the track.
		 *
		 * @param comment the track comment
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder cmt(final String comment) {
			_comment = comment;
			return this;
		}

		/**
		 * Set the description of the track.
		 *
		 * @param description the track description
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder desc(final String description) {
			_description = description;
			return this;
		}

		/**
		 * Set the source of the track.
		 *
		 * @param source the track source
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder src(final String source) {
			_source = source;
			return this;
		}

		/**
		 * Set the track links
		 *
		 * @param links the track links
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder links(final List<Link> links) {
			_links = links;
			return this;
		}

		/**
		 * Add the given {@code link} to the track
		 *
		 * @param link the link to add to the track
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder addLink(final Link link) {
			if (_links == null) {
				_links = new ArrayList<>();
			}
			_links.add(requireNonNull(link));

			return this;
		}

		/**
		 * Add the given {@code link} to the track
		 *
		 * @param href the link to add to the track
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if the given {@code href} is {@code null}
		 * @throws IllegalArgumentException if the given {@code href} is not a
		 *         valid URL
		 */
		public Builder addLink(final String href) {
			return addLink(Link.of(href));
		}

		/**
		 * Set the track number.
		 *
		 * @param number the track number
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder number(final UInt number) {
			_number = number;
			return this;
		}

		/**
		 * Set the track number.
		 *
		 * @param number the track number
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws IllegalArgumentException if the given {@code value} is smaller
		 *         than zero
		 */
		public Builder number(final int number) {
			_number = UInt.of(number);
			return this;
		}

		/**
		 * Set the track type.
		 *
		 * @param type the track type
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder type(final String type) {
			_type = type;
			return this;
		}

		/**
		 * Set the track segments of the track.
		 *
		 * @param segments the track segments
		 * @return {@code this} {@code Builder} for method chaining
		 */
		public Builder segments(final List<TrackSegment> segments) {
			_segments = segments;
			return this;
		}

		/**
		 * Add a track segment to the track.
		 *
		 * @param segment the track segment added to the track
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if the given argument is {@code null}
		 */
		public Builder addSegment(final TrackSegment segment) {
			if (_segments == null) {
				_segments = new ArrayList<>();
			}
			_segments.add(requireNonNull(segment));

			return this;
		}

		/**
		 * Add a track segment to the track, via the given builder.
		 * <pre>{@code
		 * final Track track = Track.builder()
		 *     .name("Track 1")
		 *     .description("Mountain bike tour.")
		 *     .addSegment(segment -> segment
		 *         .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(160))
		 *         .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(161))
		 *         .addPoint(p -> p.lat(48.2081743).lon(16.3738189).ele(162))))
		 *     .addSegment(segment -> segment
		 *         .addPoint(p -> p.lat(46.2081743).lon(16.3738189).ele(160))
		 *         .addPoint(p -> p.lat(47.2081743).lon(16.3738189).ele(161))
		 *         .addPoint(p -> p.lat(49.2081743).lon(16.3738189).ele(162))))
		 *     .build();
		 * }</pre>
		 *
		 * @param segment the track segment
		 * @return {@code this} {@code Builder} for method chaining
		 * @throws NullPointerException if the given argument is {@code null}
		 */
		public Builder addSegment(final Consumer<TrackSegment.Builder> segment) {
			final TrackSegment.Builder builder = TrackSegment.builder();
			segment.accept(builder);
			return addSegment(builder.build());
		}

		/**
		 * Create a new GPX track from the current builder state.
		 *
		 * @return a new GPX track from the current builder state
		 */
		public Track build() {
			return new Track(
				_name,
				_comment,
				_description,
				_source,
				_links,
				_number,
				_type,
				_segments
			);
		}
	}

	public static Builder builder() {
		return new Builder();
	}

	/* *************************************************************************
	 *  Static object creation methods
	 * ************************************************************************/

	/**
	 * Create a new {@code Track} with the given parameters.
	 *
	 * @param name the GPS name of the track
	 * @param comment the GPS comment for the track
	 * @param description user description of the track
	 * @param source the source of data. Included to give user some idea of
	 *        reliability and accuracy of data.
	 * @param links the links to external information about track
	 * @param number the GPS track number
	 * @param type the type (classification) of track
	 * @param segments the track-segments holds a list of track-points which are
	 *        logically connected in order. To represent a single GPS track
	 *        where GPS reception was lost, or the GPS receiver was turned off,
	 *        start a new track-segment for each continuous span of track data.
	 * @return a new {@code Track} with the given parameters
	 * @throws NullPointerException if the {@code links} or the {@code segments}
	 *         sequence is {@code null}
	 */
	public static Track of(
		final String name,
		final String comment,
		final String description,
		final String source,
		final List<Link> links,
		final UInt number,
		final String type,
		final List<TrackSegment> segments
	) {
		return new Track(
			name,
			comment,
			description,
			source,
			links,
			number,
			type,
			segments
		);
	}


	/* *************************************************************************
	 *  XML stream object serialization
	 * ************************************************************************/

	/**
	 * Writes this {@code Link} object to the given XML stream {@code writer}.
	 *
	 * @param writer the XML data sink
	 * @throws XMLStreamException if an error occurs
	 */
	void write(final XMLStreamWriter writer) throws XMLStreamException {
		final XMLWriter xml = new XMLWriter(writer);

		xml.write("trk",
			xml.elem("name", _name),
			xml.elem("cmt", _comment),
			xml.elem("desc", _description),
			xml.elem("src", _source),
			xml.elems(_links, Link::write),
			xml.elem("number", _number),
			xml.elem("type", _type),
			xml.elems(_segments, TrackSegment::write)
		);
	}

	@SuppressWarnings("unchecked")
	static XMLReader<Track> reader() {
		final Function<Object[], Track> create = a -> Track.of(
			(String)a[0],
			(String)a[1],
			(String)a[2],
			(String)a[3],
			(List<Link>)a[4],
			UInt.parse(a[5]),
			(String)a[6],
			(List<TrackSegment>)a[7]
		);

		return XMLReader.of(create, "trk",
			XMLReader.of("name"),
			XMLReader.of("cmt"),
			XMLReader.of("desc"),
			XMLReader.of("src"),
			XMLReader.ofList(Link.reader()),
			XMLReader.of("number"),
			XMLReader.of("type"),
			XMLReader.ofList(TrackSegment.reader())
		);
	}

}